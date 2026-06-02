package com.durian.manage.system.dao;

import com.durian.manage.system.domain.SoapConsumables;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * 物料表(SoapConsumables)表数据库访问层
 *
 * @author maven12
 * @since 2025-05-26 20:34:33
 */
public interface SoapConsumablesDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SoapConsumables queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param soapConsumables 查询条件
     * @param pageable         分页对象
     * @return 对象列表
     */
    List<SoapConsumables> queryAllByLimit(@Param("soapConsumables") SoapConsumables soapConsumables,
                                          @Param("pageable") Pageable pageable,
                                          @Param("currentUserId") Long currentUserId,
                                          @Param("superAdmin") boolean superAdmin);

    /**
     * 统计总行数（带采购时间筛选 + owner 过滤）
     */
    long count(@Param("soapConsumables") SoapConsumables soapConsumables,
               @Param("purchaseTimeStart") Date purchaseTimeStart,
               @Param("purchaseTimeEnd") Date purchaseTimeEnd,
               @Param("currentUserId") Long currentUserId,
               @Param("superAdmin") boolean superAdmin);

    /**
     * 新增数据
     *
     * @param soapConsumables 实例对象
     * @return 影响行数
     */
    int insert(SoapConsumables soapConsumables);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SoapConsumables> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SoapConsumables> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SoapConsumables> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<SoapConsumables> entities);

    /**
     * 修改数据
     *
     * @param soapConsumables 实例对象
     * @return 影响行数
     */
    int update(SoapConsumables soapConsumables);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 查询所有可用物料（status=1），按当前用户隔离
     */
    List<SoapConsumables> queryAvailable(@Param("currentUserId") Long currentUserId,
                                         @Param("superAdmin") boolean superAdmin);

    /**
     * 扣减物料库存
     *
     * @param id 物料ID
     * @param quantity 扣减数量
     * @return 影响行数
     */
    int deductQuantity(@Param("id") Integer id, @Param("quantity") Double quantity);

    /**
     * 恢复物料库存（用于撤销打包扣减场景）
     * @param id 物料ID
     * @param quantity 恢复数量
     * @return 影响行数
     */
    int addQuantity(@Param("id") Integer id, @Param("quantity") Double quantity);

}