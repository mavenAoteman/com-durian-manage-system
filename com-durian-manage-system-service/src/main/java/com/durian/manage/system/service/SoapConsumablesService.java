package com.durian.manage.system.service;

import com.durian.manage.system.domain.SoapConsumables;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 物料表(SoapConsumables)表服务接口
 *
 * @author maven12
 * @since 2025-05-26 20:34:33
 */
public interface SoapConsumablesService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SoapConsumables queryById(Integer id);

    /**
     * 分页查询
     *
     * @param soapConsumables 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<SoapConsumables> queryByPage(SoapConsumables soapConsumables, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param soapConsumables 实例对象
     * @return 实例对象
     */
    SoapConsumables insert(SoapConsumables soapConsumables);

    /**
     * 修改数据
     *
     * @param soapConsumables 实例对象
     * @return 实例对象
     */
    SoapConsumables update(SoapConsumables soapConsumables);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 查询所有可用物料（status=1）
     *
     * @return 可用物料列表
     */
    java.util.List<SoapConsumables> queryAvailable();

}
