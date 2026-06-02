package com.durian.manage.system.dao;

import com.durian.manage.system.domain.SoapTools;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * (SoapTools)表数据库访问层
 *
 * @author makejava
 * @since 2024-10-29 20:41:50
 */
public interface SoapToolsDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SoapTools queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param pageable  分页对象
     * @return 对象列表
     */
    List<SoapTools> queryAllByLimit(@Param("soapTools") SoapTools soapTools,
                                    @Param("pageable") Pageable pageable,
                                    @Param("currentUserId") Long currentUserId,
                                    @Param("superAdmin") boolean superAdmin);

    /**
     * 统计总行数
     */
    long count(@Param("soapTools") SoapTools soapTools,
               @Param("currentUserId") Long currentUserId,
               @Param("superAdmin") boolean superAdmin);

    /**
     * 新增数据
     *
     * @param soapTools 实例对象
     * @return 影响行数
     */
    int insert(SoapTools soapTools);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SoapTools> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SoapTools> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SoapTools> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<SoapTools> entities);

    /**
     * 修改数据
     *
     * @param soapTools 实例对象
     * @return 影响行数
     */
    int update(SoapTools soapTools);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

