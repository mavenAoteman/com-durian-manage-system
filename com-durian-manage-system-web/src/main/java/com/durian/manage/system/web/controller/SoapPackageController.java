package com.durian.manage.system.web.controller;

import com.durian.manage.system.domain.*;
import com.durian.manage.system.service.SoapPackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("soapPackage")
public class SoapPackageController {
    private static final Logger log = LoggerFactory.getLogger(SoapPackageController.class);

    @Resource private SoapPackageService soapPackageService;

    /** 获取可打包的皂块（已成熟+在售） */
    @GetMapping("availablePieces")
    public ResponseEntity<List<SoapPiece>> getAvailablePieces() {
        return ResponseEntity.ok(soapPackageService.getAvailablePieces());
    }

    /** 大屏统计数据 */
    @GetMapping("dashboardStats")
    public ResponseEntity<Map<String, Object>> dashboardStats() {
        return ResponseEntity.ok(soapPackageService.getDashboardStats());
    }

    /** 大屏统计数据（时间段筛选） */
    @GetMapping("dashboardStatsByRange")
    public ResponseEntity<Map<String, Object>> dashboardStatsByRange(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(soapPackageService.getDashboardStatsByRange(startDate, endDate));
    }

    /** 分页查询 */
    @GetMapping("queryByPage")
    public ResponseEntity<Page<SoapPackage>> queryByPage(
            @RequestParam int pageIndex, @RequestParam int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate endDate) {
        SoapPackage pkg = new SoapPackage();
        pkg.setStatus(status);
        return ResponseEntity.ok(soapPackageService.queryByPage(pkg, PageRequest.of(pageIndex - 1, pageSize)));
    }

    /** 详情 */
    @GetMapping("{id}")
    public ResponseEntity<SoapPackage> queryById(@PathVariable Integer id) {
        return ResponseEntity.ok(soapPackageService.queryDetail(id));
    }

    /** 创建打包 */
    @PostMapping("create")
    public ResponseEntity<SoapPackage> create(@RequestBody Map<String, Object> body) {
        SoapPackage pkg = new SoapPackage();
        pkg.setNotes((String) body.get("notes"));

        @SuppressWarnings("unchecked")
        List<Integer> pieceIds = (List<Integer>) body.get("pieceIds");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> innerList = (List<Map<String, Object>>) body.get("innerMaterials");
        List<SoapPackageMaterial> innerMat = convertMaterials(innerList, "inner");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> outerList = (List<Map<String, Object>>) body.get("outerMaterials");
        List<SoapPackageMaterial> outerMat = convertMaterials(outerList, "outer");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> giftList = (List<Map<String, Object>>) body.get("giftMaterials");
        List<SoapPackageMaterial> giftMat = convertMaterials(giftList, "gift");

        return ResponseEntity.ok(soapPackageService.create(pkg, pieceIds, innerMat, outerMat, giftMat));
    }

    /** 更新状态 */
    @PutMapping("{id}/status")
    public ResponseEntity<SoapPackage> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(soapPackageService.updateStatus(id, (Integer) body.get("status")));
    }

    /** 从打包批次中移出皂块（回退为在售状态） */
    @PostMapping("{id}/removePieces")
    public ResponseEntity<SoapPackage> removePieces(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> pieceIds = (List<Integer>) body.get("pieceIds");
        return ResponseEntity.ok(soapPackageService.removePieces(id, pieceIds));
    }

    /** 全量修改打包：皂块/内包装/外包装/赠品/备注 一次性提交，后端做 diff */
    @PutMapping("{id}")
    public ResponseEntity<SoapPackage> updatePackage(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> pieceIds = (List<Integer>) body.get("pieceIds");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> innerList = (List<Map<String, Object>>) body.get("innerMaterials");
        List<SoapPackageMaterial> innerMat = convertMaterials(innerList, "inner");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> outerList = (List<Map<String, Object>>) body.get("outerMaterials");
        List<SoapPackageMaterial> outerMat = convertMaterials(outerList, "outer");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> giftList = (List<Map<String, Object>>) body.get("giftMaterials");
        List<SoapPackageMaterial> giftMat = convertMaterials(giftList, "gift");

        String notes = (String) body.get("notes");

        return ResponseEntity.ok(soapPackageService.updatePackage(id, pieceIds, innerMat, outerMat, giftMat, notes));
    }

    /** 删除 */
    @DeleteMapping("{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable Integer id) {
        return ResponseEntity.ok(soapPackageService.deleteById(id));
    }

    private List<SoapPackageMaterial> convertMaterials(List<Map<String, Object>> list, String type) {
        if (list == null) return java.util.Collections.emptyList();
        java.util.List<SoapPackageMaterial> result = new java.util.ArrayList<>();
        for (Map<String, Object> m : list) {
            SoapPackageMaterial mat = new SoapPackageMaterial();
            mat.setConsumableId((Integer) m.get("consumableId"));
            mat.setMaterialType(type);
            mat.setQuantityUsed(m.get("quantityUsed") != null ? Double.valueOf(m.get("quantityUsed").toString()) : 0);
            mat.setUnitPrice(m.get("unitPrice") != null ? Double.valueOf(m.get("unitPrice").toString()) : 0);
            mat.setUnit((String) m.get("unit"));
            result.add(mat);
        }
        return result;
    }
}
