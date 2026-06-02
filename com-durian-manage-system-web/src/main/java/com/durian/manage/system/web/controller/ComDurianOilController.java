package com.durian.manage.system.web.controller;


import com.alibaba.fastjson.JSON;
import com.durian.manage.system.domain.ComDurianOil;
import com.durian.manage.system.service.ComDurianOilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (ComDurianOil)表控制层
 *
 * @author makejava
 * @since 2024-03-21 11:29:24
 */
@RestController
@RequestMapping("comDurianOil")
public class ComDurianOilController {

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(ComDurianOilController.class);

    /**
     * 服务对象
     */
    @Resource
    private ComDurianOilService comDurianOilService;


    /**
     * 按页查询
     *
     * @param pageIndex 页面索引
     * @param pageSize  页面大小
     * @param name      名字
     * @return {@link ResponseEntity }<{@link Page }<{@link ComDurianOil }>>
     */
    @GetMapping("queryByPage")
    public ResponseEntity<Page<ComDurianOil>> queryByPage(@RequestParam int pageIndex, @RequestParam int pageSize, @RequestParam String name) {
        log.info("pageIndex={}",pageIndex);
        PageRequest pageRequest = PageRequest.of(pageIndex - 1, pageSize);
        ComDurianOil comDurianOil = new ComDurianOil();
        comDurianOil.setOilChineseName(name);
        Page<ComDurianOil> comDurianOils = this.comDurianOilService.queryByPage(comDurianOil, pageRequest);
        log.info("comDurianOils={}", JSON.toJSONString(comDurianOils));
        return ResponseEntity.ok(comDurianOils);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<ComDurianOil> queryById(@PathVariable("id") Integer id) {
        log.info("查询数据成功");
        return ResponseEntity.ok(this.comDurianOilService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param comDurianOil 实体
     * @return 新增结果
     */
    @PostMapping("add")
    public ResponseEntity<ComDurianOil> add(@RequestBody ComDurianOil comDurianOil) {
        // 设置默认值
        if (comDurianOil.getCreator() == null) comDurianOil.setCreator("admin");
        if (comDurianOil.getModifier() == null) comDurianOil.setModifier("admin");
        if (comDurianOil.getStatus() == null) comDurianOil.setStatus("1");
        if (comDurianOil.getCreateTime() == null) comDurianOil.setCreateTime(new java.util.Date());
        if (comDurianOil.getModifyTime() == null) comDurianOil.setModifyTime(new java.util.Date());
        if (comDurianOil.getId() != null && comDurianOil.getId() > 0) {
            return ResponseEntity.ok(this.comDurianOilService.update(comDurianOil));
        }
        return ResponseEntity.ok(this.comDurianOilService.insert(comDurianOil));
    }

    /**
     * 编辑数据
     *
     * @param comDurianOil 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<ComDurianOil> edit(@RequestBody ComDurianOil comDurianOil) {
        return ResponseEntity.ok(this.comDurianOilService.update(comDurianOil));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("delete")
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.comDurianOilService.deleteById(id));
    }

    /**
     * 查询所有油脂（供皂化计算器使用）
     */
    @GetMapping("all")
    public ResponseEntity<java.util.List<ComDurianOil>> queryAll() {
        return ResponseEntity.ok(this.comDurianOilService.queryAll());
    }

}

