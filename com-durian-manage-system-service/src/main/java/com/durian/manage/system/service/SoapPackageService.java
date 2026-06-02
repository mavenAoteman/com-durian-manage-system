package com.durian.manage.system.service;

import com.durian.manage.system.domain.SoapPackage;
import com.durian.manage.system.domain.SoapPackageMaterial;
import com.durian.manage.system.domain.SoapPiece;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface SoapPackageService {
    SoapPackage queryById(Integer id);
    SoapPackage queryDetail(Integer id);
    Page<SoapPackage> queryByPage(SoapPackage pkg, PageRequest pr);
    /** 创建打包批次（含皂块、内包装物料、外包装物料、赠品，扣减库存） */
    SoapPackage create(SoapPackage pkg, List<Integer> pieceIds,
                       List<SoapPackageMaterial> innerMaterials,
                       List<SoapPackageMaterial> outerMaterials,
                       List<SoapPackageMaterial> giftMaterials);
    /** 更新状态 */
    SoapPackage updateStatus(Integer id, Integer status);
    boolean deleteById(Integer id);
    /** 从打包批次中移出指定皂块，并把它们恢复为在售(status=1) */
    SoapPackage removePieces(Integer packageId, List<Integer> pieceIds);

    /**
     * 全量修改打包记录：
     * - 皂块按 diff 处理（少的恢复在售，多的标记打包中）
     * - 所有原物料恢复库存后清空，按新列表重新插入并扣库存
     * - 重算总成本，更新备注
     * 已完成(status=3)的打包不可修改
     */
    SoapPackage updatePackage(Integer packageId, List<Integer> pieceIds,
                              List<SoapPackageMaterial> innerMaterials,
                              List<SoapPackageMaterial> outerMaterials,
                              List<SoapPackageMaterial> giftMaterials,
                              String notes);
    /** 获取可打包的皂块（已成熟+在售状态） */
    List<SoapPiece> getAvailablePieces();

    /** 大屏统计：总成本 / 总售价 / 总收益（全部历史） */
    Map<String, Object> getDashboardStats();

    /** 大屏统计：总成本 / 总售价 / 总收益（时间段筛选） */
    Map<String, Object> getDashboardStatsByRange(String startDate, String endDate);
}
