package com.durian.manage.system.dao;

import com.durian.manage.system.domain.SoapConsumablesUsage;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 物料使用明细(SoapConsumablesUsage)表数据库访问层
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
public interface SoapConsumablesUsageDao {

    SoapConsumablesUsage queryById(Integer id);

    List<SoapConsumablesUsage> queryAllByLimit(SoapConsumablesUsage soapConsumablesUsage, @Param("pageable") Pageable pageable);

    long count(SoapConsumablesUsage soapConsumablesUsage);

    List<SoapConsumablesUsage> queryBySoapId(@Param("soapId") Integer soapId);

    int insert(SoapConsumablesUsage soapConsumablesUsage);

    int insertBatch(@Param("entities") List<SoapConsumablesUsage> entities);

    int insertOrUpdateBatch(@Param("entities") List<SoapConsumablesUsage> entities);

    int update(SoapConsumablesUsage soapConsumablesUsage);

    int deleteById(Integer id);

    int deleteBySoapId(@Param("soapId") Integer soapId);

    List<SoapConsumablesUsage> queryAllWithDetail(@Param("pageable") Pageable pageable,
                                                  @Param("currentUserId") Long currentUserId,
                                                  @Param("superAdmin") boolean superAdmin);

    long countWithDetail(@Param("currentUserId") Long currentUserId,
                         @Param("superAdmin") boolean superAdmin);

    List<SoapConsumablesUsage> queryBySoapIdWithDetail(@Param("soapId") Integer soapId);
}
