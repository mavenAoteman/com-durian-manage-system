package com.durian.manage.system.service;

import com.durian.manage.system.domain.SoapConsumablesUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 物料使用明细(SoapConsumablesUsage)表服务接口
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
public interface SoapConsumablesUsageService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SoapConsumablesUsage queryById(Integer id);

    /**
     * 分页查询
     *
     * @param soapConsumablesUsage 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<SoapConsumablesUsage> queryByPage(SoapConsumablesUsage soapConsumablesUsage, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param soapConsumablesUsage 实例对象
     * @return 实例对象
     */
    SoapConsumablesUsage insert(SoapConsumablesUsage soapConsumablesUsage);

    /**
     * 修改数据
     *
     * @param soapConsumablesUsage 实例对象
     * @return 实例对象
     */
    SoapConsumablesUsage update(SoapConsumablesUsage soapConsumablesUsage);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 分页查询消耗记录（含物料名称、生产批次等关联信息）
     */
    Page<SoapConsumablesUsage> queryWithDetail(PageRequest pageRequest);

}
