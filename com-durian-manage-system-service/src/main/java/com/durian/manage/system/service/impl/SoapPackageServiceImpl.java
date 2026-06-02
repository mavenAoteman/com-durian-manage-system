package com.durian.manage.system.service.impl;

import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.dao.*;
import com.durian.manage.system.domain.*;
import com.durian.manage.system.service.SoapPackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("soapPackageService")
public class SoapPackageServiceImpl implements SoapPackageService {
    private static final Logger log = LoggerFactory.getLogger(SoapPackageServiceImpl.class);
    private static final DateTimeFormatter PKG_FMT = DateTimeFormatter.ofPattern("'PKG'yyyyMMddHHmmss");

    @Resource private SoapPackageDao soapPackageDao;
    @Resource private SoapPackagePieceDao soapPackagePieceDao;
    @Resource private SoapPackageMaterialDao soapPackageMaterialDao;
    @Resource private SoapPieceDao soapPieceDao;
    @Resource private SoapProductionDao soapProductionDao;
    @Resource private SoapConsumablesDao soapConsumablesDao;

    @Override public SoapPackage queryById(Integer id) { return soapPackageDao.queryById(id); }

    @Override
    public SoapPackage queryDetail(Integer id) {
        SoapPackage pkg = soapPackageDao.queryById(id);
        if (pkg != null) {
            pkg.setPieces(soapPackagePieceDao.queryByPackageId(id));
            pkg.setMaterials(soapPackageMaterialDao.queryByPackageId(id));
        }
        return pkg;
    }

    @Override
    public Page<SoapPackage> queryByPage(SoapPackage pkg, PageRequest pr) {
        Long uid = currentUserIdOrThrow();
        boolean superAdmin = CurrentUserHolder.isSuperAdmin();
        long total = soapPackageDao.count(pkg, uid, superAdmin);
        return new PageImpl<>(soapPackageDao.queryAllByLimit(pkg, pr, uid, superAdmin), pr, total);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoapPackage create(SoapPackage pkg, List<Integer> pieceIds,
                              List<SoapPackageMaterial> innerMaterials,
                              List<SoapPackageMaterial> outerMaterials,
                              List<SoapPackageMaterial> giftMaterials) {
        Long uid = currentUserIdOrThrow();
        pkg.setOwnerUserId(uid);
        // 1. 校验皂块：必须已成熟(生产status=5)且皂块在售(status=1)
        for (Integer pieceId : pieceIds) {
            SoapPiece piece = soapPieceDao.queryById(pieceId);
            if (piece == null) throw new IllegalArgumentException("皂块ID=" + pieceId + " 不存在");
            if (piece.getStatus() != 1) throw new IllegalArgumentException("皂块「" + piece.getPieceName() + "」不在在售状态");
            SoapProduction prod = soapProductionDao.queryById(piece.getProductionId());
            if (prod == null || prod.getStatus() != 5)
                throw new IllegalArgumentException("皂块「" + piece.getPieceName() + "」所属批次未成熟");
        }

        // 2. 生成批次号，保存打包记录
        pkg.setBatchNumber(LocalDateTime.now().format(PKG_FMT));
        pkg.setStatus(1); // 待内包装
        double totalMatCost = 0;

        // 计算物料成本（含赠品 —— 临时赠品没有 consumableId 但有 unitPrice，照样计成本）
        List<SoapPackageMaterial> allMat = new ArrayList<>();
        if (innerMaterials != null) allMat.addAll(innerMaterials);
        if (outerMaterials != null) allMat.addAll(outerMaterials);
        if (giftMaterials != null) allMat.addAll(giftMaterials);
        for (SoapPackageMaterial m : allMat) {
            totalMatCost += (m.getQuantityUsed() != null ? m.getQuantityUsed() : 0)
                          * (m.getUnitPrice() != null ? m.getUnitPrice() : 0);
        }
        pkg.setTotalMaterialCost(Math.round(totalMatCost * 100.0) / 100.0);
        soapPackageDao.insert(pkg);

        // 3. 关联皂块
        if (pieceIds != null && !pieceIds.isEmpty()) {
            List<SoapPackagePiece> ppList = pieceIds.stream().map(pid -> {
                SoapPackagePiece pp = new SoapPackagePiece();
                pp.setPackageId(pkg.getId());
                pp.setPieceId(pid);
                return pp;
            }).collect(Collectors.toList());
            soapPackagePieceDao.insertBatch(ppList);
            // 标记皂块为打包中(state=4)
            for (Integer pid : pieceIds) {
                SoapPiece sp = new SoapPiece();
                sp.setId(pid); sp.setStatus(4);
                soapPieceDao.update(sp);
            }
        }

        // 4. 保存物料使用 + 扣库存（赠品中 consumableId 为空的"临时赠品"跳过扣库存）
        if (!allMat.isEmpty()) {
            for (SoapPackageMaterial m : allMat) {
                m.setPackageId(pkg.getId());
                soapPackageMaterialDao.insert(m);
                if (m.getConsumableId() != null) {
                    SoapConsumables c = soapConsumablesDao.queryById(m.getConsumableId());
                    double deduct = gramsToUnit(m.getQuantityUsed(), c != null ? c.getUnit() : "g", c != null ? c.getConsumableType() : null);
                    soapConsumablesDao.deductQuantity(m.getConsumableId(), deduct);
                }
            }
        }

        log.info("打包批次创建: {}, 皂块数={}, 总成本={}", pkg.getBatchNumber(), pieceIds != null ? pieceIds.size() : 0, totalMatCost);
        return pkg;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoapPackage updateStatus(Integer id, Integer status) {
        Long uid = currentUserIdOrThrow();
        SoapPackage pkg = soapPackageDao.queryById(id);
        if (pkg == null) throw new IllegalArgumentException("打包记录不存在");
        assertCanModify(pkg.getOwnerUserId(), uid);

        soapPackageDao.updateStatus(id, status);

        // 如果完成打包(status=3)，标记皂块为已售
        if (status == 3) {
            List<SoapPackagePiece> pieces = soapPackagePieceDao.queryByPackageId(id);
            for (SoapPackagePiece pp : pieces) {
                SoapPiece sp = new SoapPiece();
                sp.setId(pp.getPieceId()); sp.setStatus(2); // 已售
                soapPieceDao.update(sp);
            }
        }
        return queryById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoapPackage removePieces(Integer packageId, List<Integer> pieceIds) {
        if (pieceIds == null || pieceIds.isEmpty()) {
            throw new IllegalArgumentException("请至少选择一个要移出的皂块");
        }
        Long uid = currentUserIdOrThrow();
        SoapPackage pkg = soapPackageDao.queryById(packageId);
        if (pkg == null) throw new IllegalArgumentException("打包记录不存在");
        assertCanModify(pkg.getOwnerUserId(), uid);
        soapPackagePieceDao.deleteByPackageIdAndPieceIds(packageId, pieceIds);
        for (Integer pid : pieceIds) {
            SoapPiece sp = new SoapPiece();
            sp.setId(pid); sp.setStatus(1);
            soapPieceDao.update(sp);
        }
        log.info("打包批次 {} 移出 {} 个皂块: {}", pkg.getBatchNumber(), pieceIds.size(), pieceIds);
        return queryDetail(packageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoapPackage updatePackage(Integer packageId, List<Integer> pieceIds,
                                     List<SoapPackageMaterial> innerMaterials,
                                     List<SoapPackageMaterial> outerMaterials,
                                     List<SoapPackageMaterial> giftMaterials,
                                     String notes) {
        Long uid = currentUserIdOrThrow();
        SoapPackage pkg = soapPackageDao.queryById(packageId);
        if (pkg == null) throw new IllegalArgumentException("打包记录不存在");
        assertCanModify(pkg.getOwnerUserId(), uid);

        List<SoapPackagePiece> currentPieces = soapPackagePieceDao.queryByPackageId(packageId);
        Set<Integer> currentPieceIds = currentPieces.stream().map(SoapPackagePiece::getPieceId).collect(Collectors.toSet());
        Set<Integer> targetPieceIds = pieceIds != null ? new HashSet<>(pieceIds) : new HashSet<>();

        List<Integer> piecesToRemove = currentPieceIds.stream().filter(pid -> !targetPieceIds.contains(pid)).collect(Collectors.toList());
        List<Integer> piecesToAdd = targetPieceIds.stream().filter(pid -> !currentPieceIds.contains(pid)).collect(Collectors.toList());

        if (!piecesToRemove.isEmpty()) {
            soapPackagePieceDao.deleteByPackageIdAndPieceIds(packageId, piecesToRemove);
            for (Integer pid : piecesToRemove) {
                SoapPiece sp = new SoapPiece();
                sp.setId(pid); sp.setStatus(1);
                soapPieceDao.update(sp);
            }
        }

        for (Integer pid : piecesToAdd) {
            SoapPiece piece = soapPieceDao.queryById(pid);
            if (piece == null) throw new IllegalArgumentException("皂块ID=" + pid + " 不存在");
            if (piece.getStatus() != 1) throw new IllegalArgumentException("皂块「" + piece.getPieceName() + "」不在在售状态");
            SoapProduction prod = soapProductionDao.queryById(piece.getProductionId());
            if (prod == null || prod.getStatus() != 5) {
                throw new IllegalArgumentException("皂块「" + piece.getPieceName() + "」所属批次未成熟");
            }
            SoapPackagePiece pp = new SoapPackagePiece();
            pp.setPackageId(packageId); pp.setPieceId(pid);
            soapPackagePieceDao.insert(pp);
            SoapPiece sp = new SoapPiece();
            sp.setId(pid); sp.setStatus(4);
            soapPieceDao.update(sp);
        }

        List<SoapPackageMaterial> currentMaterials = soapPackageMaterialDao.queryByPackageId(packageId);
        for (SoapPackageMaterial m : currentMaterials) {
            if (m.getConsumableId() != null) {
                SoapConsumables c = soapConsumablesDao.queryById(m.getConsumableId());
                double restore = gramsToUnit(m.getQuantityUsed() != null ? m.getQuantityUsed() : 0,
                        c != null ? c.getUnit() : "g",
                        c != null ? c.getConsumableType() : null);
                soapConsumablesDao.addQuantity(m.getConsumableId(), restore);
            }
        }
        soapPackageMaterialDao.deleteByPackageId(packageId);

        List<SoapPackageMaterial> newAll = new ArrayList<>();
        if (innerMaterials != null) newAll.addAll(innerMaterials);
        if (outerMaterials != null) newAll.addAll(outerMaterials);
        if (giftMaterials != null) newAll.addAll(giftMaterials);

        double totalCost = 0;
        for (SoapPackageMaterial m : newAll) {
            m.setPackageId(packageId);
            soapPackageMaterialDao.insert(m);
            totalCost += (m.getQuantityUsed() != null ? m.getQuantityUsed() : 0)
                       * (m.getUnitPrice() != null ? m.getUnitPrice() : 0);
            if (m.getConsumableId() != null) {
                SoapConsumables c = soapConsumablesDao.queryById(m.getConsumableId());
                double deduct = gramsToUnit(m.getQuantityUsed() != null ? m.getQuantityUsed() : 0,
                        c != null ? c.getUnit() : "g",
                        c != null ? c.getConsumableType() : null);
                soapConsumablesDao.deductQuantity(m.getConsumableId(), deduct);
            }
        }

        pkg.setTotalMaterialCost(Math.round(totalCost * 100.0) / 100.0);
        pkg.setNotes(notes);
        soapPackageDao.update(pkg);

        log.info("打包批次 {} 已修改：皂块 +{} -{}，物料 {} 项，总成本 {}",
                pkg.getBatchNumber(), piecesToAdd.size(), piecesToRemove.size(), newAll.size(), totalCost);
        return queryDetail(packageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Integer id) {
        // 恢复皂块状态
        List<SoapPackagePiece> pieces = soapPackagePieceDao.queryByPackageId(id);
        for (SoapPackagePiece pp : pieces) {
            SoapPiece sp = new SoapPiece();
            sp.setId(pp.getPieceId()); sp.setStatus(1); // 恢复在售
            soapPieceDao.update(sp);
        }
        soapPackagePieceDao.deleteByPackageId(id);
        soapPackageMaterialDao.deleteByPackageId(id);
        return soapPackageDao.deleteById(id) > 0;
    }

    private double toGrams(double qty, String unit, Integer consumableType) {
        Double r = ConsumableUnitEnum.toGrams(qty, unit, consumableType);
        return r != null ? r : qty;
    }
    private double gramsToUnit(double g, String unit, Integer consumableType) {
        Double r = ConsumableUnitEnum.fromGrams(g, unit, consumableType);
        return r != null ? r : g;
    }

    @Override
    public List<SoapPiece> getAvailablePieces() {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw com.durian.manage.system.common.exception.ApiException.unauthorized("请先登录");
        }
        // 查询所有皂块，筛选：生产状态=5(已成熟) 且 皂块状态=1(在售)
        List<SoapPiece> all = soapPieceDao.queryAll(uid, CurrentUserHolder.isSuperAdmin());
        return all.stream().filter(p -> {
            if (p.getStatus() != 1) return false;
            SoapProduction prod = soapProductionDao.queryById(p.getProductionId());
            return prod != null && prod.getStatus() == 5;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        Long uid = CurrentUserHolder.getUserId();
        boolean superAdmin = CurrentUserHolder.isSuperAdmin();
        if (uid == null) {
            throw com.durian.manage.system.common.exception.ApiException.unauthorized("请先登录");
        }
        List<SoapProduction> allProds = soapProductionDao.queryAll(uid, superAdmin);
        double totalCost = 0;
        double totalSalesRevenue = 0;
        for (SoapProduction p : allProds) {
            if (p.getStatus() != null && p.getStatus() == 5) {
                if (p.getTotalCost() != null) totalCost += p.getTotalCost();
                if (p.getTotalPiecePrice() != null) totalSalesRevenue += p.getTotalPiecePrice();
            }
        }

        long completedPkgCount = soapPackageDao.queryAll(uid, superAdmin).stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 3).count();

        stats.put("totalMaterialCost", Math.round(totalCost * 100.0) / 100.0);
        stats.put("totalSalesRevenue", Math.round(totalSalesRevenue * 100.0) / 100.0);
        stats.put("totalProfit", Math.round((totalSalesRevenue - totalCost) * 100.0) / 100.0);
        stats.put("completedPkgCount", completedPkgCount);
        return stats;
    }

    @Override
    public Map<String, Object> getDashboardStatsByRange(String startDate, String endDate) {
        return getDashboardStats();
    }

    private Long currentUserIdOrThrow() {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw com.durian.manage.system.common.exception.ApiException.unauthorized("请先登录");
        }
        return uid;
    }

    private void assertCanModify(Long ownerUserId, Long currentUserId) {
        if (CurrentUserHolder.isSuperAdmin()) {
            return;
        }
        if (ownerUserId == null || !ownerUserId.equals(currentUserId)) {
            throw com.durian.manage.system.common.exception.ApiException.forbidden("无权操作他人的数据");
        }
    }
}
