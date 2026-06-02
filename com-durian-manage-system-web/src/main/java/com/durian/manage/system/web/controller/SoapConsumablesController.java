package com.durian.manage.system.web.controller;

import com.alibaba.fastjson.JSON;
import com.durian.manage.system.domain.ConsumableTypeEnum;
import com.durian.manage.system.domain.ConsumableUnitEnum;
import com.durian.manage.system.domain.SoapConsumables;
import com.durian.manage.system.service.SoapConsumablesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 物料表(SoapConsumables)表控制层
 *
 * @author maven12
 * @since 2025-05-26 20:34:33
 */
@RestController
@RequestMapping("soapConsumables")
public class SoapConsumablesController {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(SoapConsumablesController.class);

    /**
     * 服务对象
     */
    @Resource
    private SoapConsumablesService soapConsumablesService;

    /**
     * 获取物料类型枚举值
     *
     * @return 物料类型枚举列表
     */
    @GetMapping("consumableTypes")
    public ResponseEntity<Map<String, Object>> getConsumableTypes() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        // 将枚举转换为包含code和desc的map列表
        List<Map<String, Object>> consumableTypeList = new ArrayList<>();
        for (ConsumableTypeEnum type : ConsumableTypeEnum.values()) {
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put("code", type.getCode());
            typeMap.put("desc", type.getDesc());
            consumableTypeList.add(typeMap);
        }
        result.put("data", consumableTypeList);
        result.put("message", "获取物料类型成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 获取物料单位枚举值
     *
     * @return 物料单位枚举列表
     */
    @GetMapping("consumableUnits")
    public ResponseEntity<Map<String, Object>> getConsumableUnits() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        List<Map<String, Object>> unitList = new ArrayList<>();
        for (ConsumableUnitEnum unit : ConsumableUnitEnum.values()) {
            Map<String, Object> unitMap = new HashMap<>();
            unitMap.put("code", unit.getCode());
            unitMap.put("desc", unit.getDesc());
            unitList.add(unitMap);
        }
        result.put("data", unitList);
        result.put("message", "获取物料单位成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 分页查询
     *
     * @param pageIndex 页面索引
     * @param pageSize  页面大小
     * @param name      名字
     * @return 查询结果
     */
    @GetMapping("queryByPage")
    public ResponseEntity<Page<SoapConsumables>> queryByPage(
            @RequestParam int pageIndex, 
            @RequestParam int pageSize, 
            @RequestParam String name,
            @RequestParam(required = false) Integer consumableType,
            @RequestParam(required = false) String purchaseChannel,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer status,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date endDate) {
        log.info("SoapConsumablesController.queryByPage.queryByPage,name={},page={},size={}", name, pageIndex, pageSize);
        PageRequest pageRequest = PageRequest.of(pageIndex - 1, pageSize);
        SoapConsumables soapTools = new SoapConsumables();
        soapTools.setName(name);
        soapTools.setConsumableType(consumableType);
        soapTools.setPurchaseChannel(purchaseChannel);
        soapTools.setBrand(brand);
        soapTools.setPurchaseTime(startDate);
        // 使用expiryDate字段传递结束时间
        if (endDate != null) {
            soapTools.setExpiryDate(endDate);
        }
        
        // 如果没有指定状态参数，则默认只查询状态为1(可用)的数据
        if (status == null) {
            soapTools.setStatus(1);
        } else {
            // 否则使用传入的状态参数
            soapTools.setStatus(status);
        }
        
        Page<SoapConsumables> result = this.soapConsumablesService.queryByPage(soapTools, pageRequest);
        log.info("SoapToolsController.queryByPage,result={}", JSON.toJSONString(result));
        return ResponseEntity.ok(result);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<SoapConsumables> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.soapConsumablesService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param soapConsumables 实体
     * @return 新增结果
     */
    @PostMapping("add")
    public ResponseEntity<SoapConsumables> add(@RequestBody SoapConsumables soapConsumables) {
        soapConsumables.setCreatedBy("admin");
        soapConsumables.setUpdatedBy("admin");
        
        // 如果采购时间为空，则设置为当前时间
        if (soapConsumables.getPurchaseTime() == null) {
            soapConsumables.setPurchaseTime(new Date());
        }
        
        log.info("SoapConsumablesController,add={}", JSON.toJSONString(soapConsumables));
        if (soapConsumables.getId() != null && soapConsumables.getId() > 0) {
            log.info("SoapConsumablesController,update={}", JSON.toJSONString(soapConsumables));
            return ResponseEntity.ok(this.soapConsumablesService.update(soapConsumables));
        }
        return ResponseEntity.ok(this.soapConsumablesService.insert(soapConsumables));
    }

    /**
     * 编辑数据
     *
     * @param soapConsumables 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<SoapConsumables> edit(@RequestBody SoapConsumables soapConsumables) {
        return ResponseEntity.ok(this.soapConsumablesService.update(soapConsumables));
    }

    /**
     * 删除数据(逻辑删除)
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("delete")
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.soapConsumablesService.deleteById(id));
    }

}