package com.durian.manage.system.service;

import com.durian.manage.system.domain.SoapPiece;
import com.durian.manage.system.domain.SoapProduction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 手工皂生产记录(SoapProduction)表服务接口
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
public interface SoapProductionService {

    SoapProduction queryById(Integer id);

    /**
     * 查询生产记录详情（含物料使用明细和皂块列表）
     */
    SoapProduction queryDetailById(Integer id);

    Page<SoapProduction> queryByPage(SoapProduction soapProduction, PageRequest pageRequest);

    /**
     * 新增生产批次（含物料使用列表，自动计算成本并扣减库存）
     */
    SoapProduction insert(SoapProduction soapProduction);

    SoapProduction update(SoapProduction soapProduction);

    /**
     * 更新生产状态（含成熟、切块入库等状态流转）
     */
    SoapProduction updateStatus(Integer id, Integer newStatus, Double actualWeight);

    /**
     * 为生产批次添加皂块（切块分装）
     */
    List<SoapPiece> addPieces(Integer productionId, List<SoapPiece> pieces);

    List<SoapPiece> getPieces(Integer productionId);

    SoapPiece updatePiece(SoapPiece piece);

    /**
     * 批量更新皂块（删除旧皂块重新插入，不改变生产状态）
     */
    List<SoapPiece> updatePieces(Integer productionId, List<SoapPiece> pieces);

    boolean deletePiece(Integer pieceId);

    boolean deleteById(Integer id);
}
