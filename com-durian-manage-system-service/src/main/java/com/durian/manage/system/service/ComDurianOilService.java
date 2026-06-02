package com.durian.manage.system.service;

import com.durian.manage.system.domain.ComDurianOil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * (ComDurianOil)表服务接口
 *
 * @author makejava
 * @since 2024-03-21 11:29:24
 */
public interface ComDurianOilService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ComDurianOil queryById(Integer id);

    /**
     * 分页查询
     *
     * @param comDurianOil 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<ComDurianOil> queryByPage(ComDurianOil comDurianOil, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param comDurianOil 实例对象
     * @return 实例对象
     */
    ComDurianOil insert(ComDurianOil comDurianOil);

    /**
     * 修改数据
     *
     * @param comDurianOil 实例对象
     * @return 实例对象
     */
    ComDurianOil update(ComDurianOil comDurianOil);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 查询所有油脂（供皂化计算器使用）
     */
    java.util.List<ComDurianOil> queryAll();

}
