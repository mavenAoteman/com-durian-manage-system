package com.durian.manage.system.web.controller;

import com.alibaba.fastjson.JSON;
import com.durian.manage.system.domain.SoapTools;
import com.durian.manage.system.service.SoapToolsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (SoapTools)表控制层
 *
 * @author makejava
 * @since 2024-10-29 20:41:49
 */
@RestController
@RequestMapping("soapTools")
public class SoapToolsController {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(SoapToolsController.class);

    /**
     * 服务对象
     */
    @Resource
    private SoapToolsService soapToolsService;

    /**
     * 分页查询
     *
     * @return 查询结果
     */
    @GetMapping("queryByPage")
    public ResponseEntity<Page<SoapTools>> queryByPage(@RequestParam int pageIndex, @RequestParam int pageSize, @RequestParam String name) {
        log.info("SoapToolsController.queryByPage,name={},page={},size={}",name, pageIndex, pageSize);
        PageRequest pageRequest = PageRequest.of(pageIndex-1, pageSize);
        SoapTools soapTools = new SoapTools();
        soapTools.setName(name);
        Page<SoapTools> result = this.soapToolsService.queryByPage(soapTools, pageRequest);
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
    public ResponseEntity<SoapTools> queryById(@PathVariable("id") Integer id) {
        log.info("SoapToolsController.queryById,id={}", id);
        return ResponseEntity.ok(this.soapToolsService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param soapTools 实体
     * @return 新增结果
     */
    @PostMapping("add")
    public ResponseEntity<SoapTools> add(@RequestBody SoapTools soapTools) {
        log.info("SoapToolsController,add={}", JSON.toJSONString(soapTools));
        soapTools.setCreatedBy("admin");
        soapTools.setUpdatedBy("admin");
        if (soapTools.getId() != null && soapTools.getId() > 0) {
            log.info("SoapToolsController,update={}", JSON.toJSONString(soapTools));
            return ResponseEntity.ok(this.soapToolsService.update(soapTools));
        }
        return ResponseEntity.ok(this.soapToolsService.insert(soapTools));
    }

//    /**
//     * 编辑数据
//     *
//     * @param soapTools 实体
//     * @return 编辑结果
//     */
//    @PutMapping
//    public ResponseEntity<SoapTools> edit(SoapTools soapTools) {
//        return ResponseEntity.ok(this.soapToolsService.update(soapTools));
//    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("delete")
    public ResponseEntity<Boolean> deleteById(Integer id) {
        log.info("SoapToolsController,deleteById={}", id);
        return ResponseEntity.ok(this.soapToolsService.deleteById(id));
    }

}

