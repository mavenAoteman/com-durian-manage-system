package com.durian.manage.system.dao;

import com.durian.manage.system.domain.SoapProduction;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

/**
 * 手工皂生产记录(SoapProduction)表数据库访问层
 *
 * @author maven12
 * @since 2025-05-27 20:34:09
 */
public interface SoapProductionDao {

    SoapProduction queryById(Integer id);

    List<SoapProduction> queryAllByLimit(@Param("soapProduction") SoapProduction soapProduction,
                                         @Param("pageable") Pageable pageable,
                                         @Param("currentUserId") Long currentUserId,
                                         @Param("superAdmin") boolean superAdmin);

    List<SoapProduction> queryAll(@Param("currentUserId") Long currentUserId,
                                  @Param("superAdmin") boolean superAdmin);

    long count(@Param("soapProduction") SoapProduction soapProduction,
               @Param("currentUserId") Long currentUserId,
               @Param("superAdmin") boolean superAdmin);

    int insert(SoapProduction soapProduction);

    int insertBatch(@Param("entities") List<SoapProduction> entities);

    int insertOrUpdateBatch(@Param("entities") List<SoapProduction> entities);

    int update(SoapProduction soapProduction);

    int updateStatus(@Param("id") Integer id,
                     @Param("status") Integer status,
                     @Param("cutWeight") Double cutWeight,
                     @Param("actualWeight") Double actualWeight,
                     @Param("actualMaturationDate") Date actualMaturationDate);

    int deleteById(Integer id);
}
