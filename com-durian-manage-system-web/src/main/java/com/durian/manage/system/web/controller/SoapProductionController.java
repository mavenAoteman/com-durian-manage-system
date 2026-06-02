package com.durian.manage.system.web.controller;

import com.alibaba.fastjson.JSON;
import com.durian.manage.system.domain.SoapConsumables;
import com.durian.manage.system.domain.SoapPiece;
import com.durian.manage.system.domain.SoapProduction;
import com.durian.manage.system.service.SoapConsumablesService;
import com.durian.manage.system.service.SoapProductionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手工皂生产记录(SoapProduction)表控制层
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
@RestController
@RequestMapping("soapProduction")
public class SoapProductionController {
    private static final Logger log = LoggerFactory.getLogger(SoapProductionController.class);

    @Resource
    private SoapProductionService soapProductionService;

    @Resource
    private SoapConsumablesService soapConsumablesService;

    /**
     * 获取可用物料列表（供生产表单选择）
     */
    @GetMapping("consumables")
    public ResponseEntity<Map<String, Object>> getAvailableConsumables() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        List<SoapConsumables> list = soapConsumablesService.queryAvailable();
        result.put("data", list);
        result.put("message", "获取可用物料列表成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 分页查询
     */
    @GetMapping("queryByPage")
    public ResponseEntity<Page<SoapProduction>> queryByPage(
            @RequestParam int pageIndex,
            @RequestParam int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        log.info("SoapProductionController.queryByPage, name={}, status={}, page={}, size={}", name, status, pageIndex, pageSize);
        PageRequest pageRequest = PageRequest.of(pageIndex - 1, pageSize);
        SoapProduction soapProduction = new SoapProduction();
        soapProduction.setName(name);
        soapProduction.setStatus(status);
        Page<SoapProduction> result = this.soapProductionService.queryByPage(soapProduction, pageRequest);
        log.info("SoapProductionController.queryByPage, totalElements={}", result.getTotalElements());
        return ResponseEntity.ok(result);
    }

    /**
     * 查询生产记录详情（含物料使用明细+皂块列表）
     */
    @GetMapping("{id}")
    public ResponseEntity<SoapProduction> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.soapProductionService.queryDetailById(id));
    }

    /**
     * 新增生产批次（含物料使用列表）
     */
    @PostMapping("add")
    public ResponseEntity<SoapProduction> add(@RequestBody SoapProduction soapProduction) {
        soapProduction.setCreatedBy("admin");
        soapProduction.setUpdatedBy("admin");
        log.info("SoapProductionController.add={}", JSON.toJSONString(soapProduction));
        if (soapProduction.getId() != null && soapProduction.getId() > 0) {
            return ResponseEntity.ok(this.soapProductionService.update(soapProduction));
        }
        return ResponseEntity.ok(this.soapProductionService.insert(soapProduction));
    }

    /**
     * 编辑生产记录基本信息
     */
    @PutMapping
    public ResponseEntity<SoapProduction> edit(@RequestBody SoapProduction soapProduction) {
        soapProduction.setUpdatedBy("admin");
        return ResponseEntity.ok(this.soapProductionService.update(soapProduction));
    }

    /**
     * 更新生产状态（成熟/切块等状态流转）
     */
    @PutMapping("{id}/status")
    public ResponseEntity<SoapProduction> updateStatus(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Object> body) {
        Integer newStatus = (Integer) body.get("status");
        Double actualWeight = body.get("actualWeight") != null ?
                Double.valueOf(body.get("actualWeight").toString()) : null;
        log.info("SoapProductionController.updateStatus, id={}, newStatus={}, actualWeight={}", id, newStatus, actualWeight);
        return ResponseEntity.ok(this.soapProductionService.updateStatus(id, newStatus, actualWeight));
    }

    /**
     * 获取生产批次的皂块列表
     */
    @GetMapping("{id}/pieces")
    public ResponseEntity<List<SoapPiece>> getPieces(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.soapProductionService.getPieces(id));
    }

    /**
     * 批量添加皂块（切块分装）
     */
    @PostMapping("{id}/pieces")
    public ResponseEntity<List<SoapPiece>> addPieces(
            @PathVariable("id") Integer id,
            @RequestBody List<SoapPiece> pieces) {
        log.info("SoapProductionController.addPieces, productionId={}, pieces={}", id, JSON.toJSONString(pieces));
        return ResponseEntity.ok(this.soapProductionService.addPieces(id, pieces));
    }

    /**
     * 批量更新皂块（编辑已切块的皂块信息）
     */
    @PutMapping("{id}/pieces/batch")
    public ResponseEntity<List<SoapPiece>> updatePieces(
            @PathVariable("id") Integer id,
            @RequestBody List<SoapPiece> pieces) {
        log.info("SoapProductionController.updatePieces, productionId={}, count={}", id, pieces.size());
        return ResponseEntity.ok(this.soapProductionService.updatePieces(id, pieces));
    }

    /**
     * 更新皂块（定价等）
     */
    @PutMapping("pieces/{pieceId}")
    public ResponseEntity<SoapPiece> updatePiece(
            @PathVariable("pieceId") Integer pieceId,
            @RequestBody SoapPiece piece) {
        piece.setId(pieceId);
        return ResponseEntity.ok(this.soapProductionService.updatePiece(piece));
    }

    /**
     * 删除皂块
     */
    @DeleteMapping("pieces/{pieceId}")
    public ResponseEntity<Boolean> deletePiece(@PathVariable("pieceId") Integer pieceId) {
        return ResponseEntity.ok(this.soapProductionService.deletePiece(pieceId));
    }

    /**
     * 删除生产记录
     */
    @DeleteMapping("delete")
    public ResponseEntity<Boolean> deleteById(Integer id) {
        log.info("SoapProductionController.deleteById, id={}", id);
        return ResponseEntity.ok(this.soapProductionService.deleteById(id));
    }
}
