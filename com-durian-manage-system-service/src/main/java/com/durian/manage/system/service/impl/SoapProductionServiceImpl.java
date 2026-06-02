package com.durian.manage.system.service.impl;

import com.durian.manage.system.common.exception.ApiException;
import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.dao.SoapConsumablesDao;
import com.durian.manage.system.dao.SoapConsumablesUsageDao;
import com.durian.manage.system.dao.SoapPackagePieceDao;
import com.durian.manage.system.dao.SoapPieceDao;
import com.durian.manage.system.dao.SoapProductionDao;
import com.durian.manage.system.domain.ConsumableUnitEnum;
import com.durian.manage.system.domain.SoapConsumables;
import com.durian.manage.system.domain.SoapConsumablesUsage;
import com.durian.manage.system.domain.SoapPiece;
import com.durian.manage.system.domain.SoapProduction;
import com.durian.manage.system.service.SoapProductionService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 手工皂生产记录(SoapProduction)表服务实现类
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
@Service("soapProductionService")
public class SoapProductionServiceImpl implements SoapProductionService {
    private static final Logger log = LoggerFactory.getLogger(SoapProductionServiceImpl.class);
    private static final DateTimeFormatter BATCH_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private SoapProductionDao soapProductionDao;

    @Resource
    private SoapConsumablesDao soapConsumablesDao;

    @Resource
    private SoapConsumablesUsageDao soapConsumablesUsageDao;

    @Resource
    private SoapPieceDao soapPieceDao;

    @Resource
    private SoapPackagePieceDao soapPackagePieceDao;

    @Override
    public SoapProduction queryById(Integer id) {
        return this.soapProductionDao.queryById(id);
    }

    @Override
    public SoapProduction queryDetailById(Integer id) {
        SoapProduction production = this.soapProductionDao.queryById(id);
        if (production != null) {
            List<SoapConsumablesUsage> usages = this.soapConsumablesUsageDao.queryBySoapIdWithDetail(id);
            // 后端统一计算 subtotal
            for (SoapConsumablesUsage u : usages) {
                double qty = u.getQuantityUsed() != null ? u.getQuantityUsed() : 0;
                double price = u.getUnitPrice() != null ? u.getUnitPrice() : 0;
                u.setSubtotal(Math.round(qty * price * 100.0) / 100.0);
            }
            production.setUsages(usages);
            List<SoapPiece> pieces = this.soapPieceDao.queryByProductionId(id);
            production.setPieces(pieces);
        }
        return production;
    }

    @Override
    public Page<SoapProduction> queryByPage(SoapProduction soapProduction, PageRequest pageRequest) {
        Long uid = currentUserIdOrThrow();
        boolean superAdmin = CurrentUserHolder.isSuperAdmin();
        long total = this.soapProductionDao.count(soapProduction, uid, superAdmin);
        return new PageImpl<>(this.soapProductionDao.queryAllByLimit(soapProduction, pageRequest, uid, superAdmin),
                pageRequest, total);
    }

    /**
     * 新增生产批次
     * 1. 校验物料库存
     * 2. 生成批次号
     * 3. 计算总成本
     * 4. 保存生产记录
     * 5. 保存物料使用明细
     * 6. 扣减物料库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoapProduction insert(SoapProduction soapProduction) {
        Long uid = currentUserIdOrThrow();
        soapProduction.setOwnerUserId(uid);

        List<SoapConsumablesUsage> usages = soapProduction.getUsages();
        if (usages == null) {
            usages = new ArrayList<>();
        }

        // 1. 校验所有物料库存充足
        if (!usages.isEmpty()) {
        for (SoapConsumablesUsage usage : usages) {
            SoapConsumables consumable = soapConsumablesDao.queryById(usage.getConsumableId());
            if (consumable == null || consumable.getStatus() != 1) {
                throw new IllegalArgumentException("物料(ID=" + usage.getConsumableId() + ")不可用");
            }
            // 统一转克(g)比较：物料库存 × 单位换算系数 vs 使用量(g)
            double stockInGrams = toGrams(consumable.getQuantity(), consumable.getUnit(), consumable.getConsumableType());
            if (stockInGrams < usage.getQuantityUsed()) {
                throw new IllegalArgumentException("物料「" + consumable.getName() + "」库存不足，当前库存: "
                    + consumable.getQuantity() + consumable.getUnit() + " (≈" + String.format("%.1f", stockInGrams) + "g)");
            }
            // 快照单价（前端已将 unitPrice 归一化为克单价）
            if (usage.getUnitPrice() == null) {
                usage.setUnitPrice(consumable.getUnitPrice() != null ? consumable.getUnitPrice() : 0);
            }
            // 记录单位
            if (usage.getUnit() == null) {
                usage.setUnit(consumable.getUnit());
            }
        }
        } // end if !usages.isEmpty()

        // 2. 生成批次号
        soapProduction.setBatchNumber(LocalDateTime.now().format(BATCH_NO_FORMATTER));
        if (soapProduction.getProductionDate() == null) {
            soapProduction.setProductionDate(new Date());
        }
        if (soapProduction.getStatus() == null) {
            soapProduction.setStatus(1); // 生产中
        }

        // 3. 计算总成本（unitPrice 已是克单价，直接克重×单价）
        double totalCost = 0;
        if (!usages.isEmpty()) {
            for (SoapConsumablesUsage usage : usages) {
                totalCost += (usage.getQuantityUsed() != null ? usage.getQuantityUsed() : 0)
                          * (usage.getUnitPrice() != null ? usage.getUnitPrice() : 0);
            }
        }
        soapProduction.setTotalCost(Math.round(totalCost * 100.0) / 100.0);

        // 4. 保存生产记录
        this.soapProductionDao.insert(soapProduction);

        // 5. 批量保存物料使用明细
        if (!usages.isEmpty()) {
            for (SoapConsumablesUsage usage : usages) {
                usage.setSoapId(soapProduction.getId());
                this.soapConsumablesUsageDao.insert(usage);
            }

            // 6. 逐一扣减物料库存（将克转回物料原始单位）
            for (SoapConsumablesUsage usage : usages) {
                SoapConsumables c = soapConsumablesDao.queryById(usage.getConsumableId());
                double deductAmount = gramsToUnit(usage.getQuantityUsed(), c != null ? c.getUnit() : "g", c != null ? c.getConsumableType() : null);
                int affected = this.soapConsumablesDao.deductQuantity(usage.getConsumableId(), deductAmount);
                if (affected == 0) {
                    throw new RuntimeException("扣减物料「ID=" + usage.getConsumableId() + "」失败，可能库存已被其他操作修改");
                }
            }
        }

        log.info("生产批次创建成功: batchNumber={}, totalCost={}, usagesCount={}",
                soapProduction.getBatchNumber(), soapProduction.getTotalCost(), usages.size());
        return soapProduction;
    }

    /**
     * 更新生产状态
     * 1 -> 2: 进入成熟期（开始晾皂）
     * 2 -> 3: 标记已成熟
     * 3 -> 4: 切块入库（需要传入 actualWeight）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SoapProduction updateStatus(Integer id, Integer newStatus, Double weight) {
        Long uid = currentUserIdOrThrow();
        SoapProduction production = this.soapProductionDao.queryById(id);
        if (production == null) {
            throw new IllegalArgumentException("生产记录不存在");
        }
        assertCanModify(production.getOwnerUserId(), uid);

        Double cutWeight = null;
        Double actualWeight = null;
        Date actualMaturationDate = null;

        if (newStatus == 3) {
            // 切块：记录切块后总重量
            cutWeight = weight;
        } else if (newStatus == 5) {
            // 成熟：记录成熟后实际重量 + 成熟日期
            actualWeight = weight;
            actualMaturationDate = new Date();
        }

        this.soapProductionDao.updateStatus(id, newStatus, cutWeight, actualWeight, actualMaturationDate);
        return this.queryById(id);
    }

    /**
     * 切块分装：批量添加皂块
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SoapPiece> addPieces(Integer productionId, List<SoapPiece> pieces) {
        Long uid = currentUserIdOrThrow();
        SoapProduction production = this.soapProductionDao.queryById(productionId);
        if (production == null) {
            throw new IllegalArgumentException("生产记录不存在");
        }
        assertCanModify(production.getOwnerUserId(), uid);

        // 清除已有皂块
        this.soapPieceDao.deleteByProductionId(productionId);

        // 批量插入新皂块
        for (SoapPiece piece : pieces) {
            piece.setProductionId(productionId);
            piece.setOwnerUserId(production.getOwnerUserId());
            if (piece.getStatus() == null) {
                piece.setStatus(1); // 默认在售
            }
        }
        this.soapPieceDao.insertBatch(pieces);

        // 计算切块总重量并更新状态为已切块
        double cutWeight = pieces.stream().filter(p -> p.getWeight() != null).mapToDouble(SoapPiece::getWeight).sum();
        this.soapProductionDao.updateStatus(productionId, 3, cutWeight, null, null);

        return this.soapPieceDao.queryByProductionId(productionId);
    }

    @Override
    public List<SoapPiece> getPieces(Integer productionId) {
        return this.soapPieceDao.queryByProductionId(productionId);
    }

    /**
     * 批量更新皂块（diff 模式：保留 id 的更新、新加的插入、缺失的删除）
     * 删除前做 FK 保护：已被打包关联的皂块不能删
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SoapPiece> updatePieces(Integer productionId, List<SoapPiece> pieces) {
        Long uid = currentUserIdOrThrow();
        SoapProduction production = this.soapProductionDao.queryById(productionId);
        if (production == null) {
            throw new IllegalArgumentException("生产记录不存在");
        }
        assertCanModify(production.getOwnerUserId(), uid);

        List<SoapPiece> existing = this.soapPieceDao.queryByProductionId(productionId);
        Map<Integer, SoapPiece> existingById = new HashMap<>();
        for (SoapPiece p : existing) {
            if (p.getId() != null) existingById.put(p.getId(), p);
        }
        List<Integer> inputIds = new ArrayList<>();
        for (SoapPiece p : pieces) {
            if (p.getId() != null) inputIds.add(p.getId());
        }

        // 1) 计算要删除的（旧有、新无）
        List<Integer> toDeleteIds = new ArrayList<>();
        for (SoapPiece p : existing) {
            if (p.getId() != null && !inputIds.contains(p.getId())) {
                toDeleteIds.add(p.getId());
            }
        }
        // FK 保护：被打包引用的不能删
        if (!toDeleteIds.isEmpty()) {
            List<Integer> referenced = this.soapPackagePieceDao.queryReferencedPieceIds(toDeleteIds);
            if (!referenced.isEmpty()) {
                String names = existing.stream()
                        .filter(p -> referenced.contains(p.getId()))
                        .map(p -> p.getPieceName() != null ? p.getPieceName() : ("ID=" + p.getId()))
                        .collect(Collectors.joining("、"));
                throw new IllegalArgumentException("以下皂块已在打包批次中，请先在「打包发货」里移出再删除：" + names);
            }
            for (Integer id : toDeleteIds) {
                this.soapPieceDao.deleteById(id);
            }
        }

        // 2) 更新已有的（按 id 命中）+ 收集新增的
        List<SoapPiece> toInsert = new ArrayList<>();
        for (SoapPiece p : pieces) {
            if (p.getStatus() == null) p.setStatus(1);
            p.setProductionId(productionId);
            p.setOwnerUserId(production.getOwnerUserId());
            if (p.getId() != null && existingById.containsKey(p.getId())) {
                this.soapPieceDao.update(p);
            } else {
                p.setId(null);
                toInsert.add(p);
            }
        }
        if (!toInsert.isEmpty()) {
            this.soapPieceDao.insertBatch(toInsert);
        }

        return this.soapPieceDao.queryByProductionId(productionId);
    }

    @Override
    public SoapPiece updatePiece(SoapPiece piece) {
        this.soapPieceDao.update(piece);
        return this.soapPieceDao.queryById(piece.getId());
    }

    @Override
    public boolean deletePiece(Integer pieceId) {
        return this.soapPieceDao.deleteById(pieceId) > 0;
    }

    @Override
    public SoapProduction update(SoapProduction soapProduction) {
        Long uid = currentUserIdOrThrow();
        SoapProduction existing = this.soapProductionDao.queryById(soapProduction.getId());
        if (existing == null) {
            throw ApiException.badRequest("生产记录不存在");
        }
        assertCanModify(existing.getOwnerUserId(), uid);
        this.soapProductionDao.update(soapProduction);
        return this.queryById(soapProduction.getId());
    }

    private Long currentUserIdOrThrow() {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw ApiException.unauthorized("请先登录");
        }
        return uid;
    }

    private void assertCanModify(Long ownerUserId, Long currentUserId) {
        if (CurrentUserHolder.isSuperAdmin()) {
            return;
        }
        if (ownerUserId == null || !ownerUserId.equals(currentUserId)) {
            throw ApiException.forbidden("无权操作他人的数据");
        }
    }

    // ---- 单位换算工具（含油脂密度 0.92） ----
    private static double toGrams(double qty, String unit, Integer consumableType) {
        Double result = ConsumableUnitEnum.toGrams(qty, unit, consumableType);
        return result != null ? result : qty;
    }

    private static double gramsToUnit(double grams, String unit, Integer consumableType) {
        Double result = ConsumableUnitEnum.fromGrams(grams, unit, consumableType);
        return result != null ? result : grams;
    }

    @Override
    public boolean deleteById(Integer id) {
        Long uid = currentUserIdOrThrow();
        SoapProduction existing = this.soapProductionDao.queryById(id);
        if (existing == null) {
            return false;
        }
        assertCanModify(existing.getOwnerUserId(), uid);
        // 删除关联的物料使用明细和皂块
        this.soapConsumablesUsageDao.deleteBySoapId(id);
        this.soapPieceDao.deleteByProductionId(id);
        return this.soapProductionDao.deleteById(id) > 0;
    }
}
