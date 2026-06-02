package com.durian.manage.system.web.controller;

import com.durian.manage.system.domain.SoapConsumablesUsage;
import com.durian.manage.system.service.SoapConsumablesUsageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 物料使用明细(SoapConsumablesUsage)表控制层
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
@RestController
@RequestMapping("soapConsumablesUsage")
public class SoapConsumablesUsageController {
    @Resource
    private SoapConsumablesUsageService soapConsumablesUsageService;

    /**
     * 分页查询消耗记录（含物料名称、品牌、生产批次等关联信息）
     */
    @GetMapping("queryByPage")
    public ResponseEntity<Page<SoapConsumablesUsage>> queryByPage(
            @RequestParam int pageIndex,
            @RequestParam int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageIndex - 1, pageSize);
        return ResponseEntity.ok(this.soapConsumablesUsageService.queryWithDetail(pageRequest));
    }

    @GetMapping("{id}")
    public ResponseEntity<SoapConsumablesUsage> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.soapConsumablesUsageService.queryById(id));
    }

    @PostMapping
    public ResponseEntity<SoapConsumablesUsage> add(SoapConsumablesUsage soapConsumablesUsage) {
        return ResponseEntity.ok(this.soapConsumablesUsageService.insert(soapConsumablesUsage));
    }

    @PutMapping
    public ResponseEntity<SoapConsumablesUsage> edit(SoapConsumablesUsage soapConsumablesUsage) {
        return ResponseEntity.ok(this.soapConsumablesUsageService.update(soapConsumablesUsage));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.soapConsumablesUsageService.deleteById(id));
    }
}
