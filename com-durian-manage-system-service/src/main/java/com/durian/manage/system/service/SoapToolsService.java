package com.durian.manage.system.service;

import com.durian.manage.system.domain.SoapTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (SoapTools)表服务接口
 *
 * @author makejava
 * @since 2024-10-29 20:41:51
 */
public interface SoapToolsService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SoapTools queryById(Integer id);

    /**
     * 分页查询
     *
     * @param soapTools   筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    Page<SoapTools> queryByPage(SoapTools soapTools, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param soapTools 实例对象
     * @return 实例对象
     */
    SoapTools insert(SoapTools soapTools);

    /**
     * 修改数据
     *
     * @param soapTools 实例对象
     * @return 实例对象
     */
    SoapTools update(SoapTools soapTools);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}
