package com.durian.manage.system.dao;

import com.durian.manage.system.domain.SoapPackage;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SoapPackageDao {
    SoapPackage queryById(Integer id);

    List<SoapPackage> queryAll(@Param("currentUserId") Long currentUserId,
                               @Param("superAdmin") boolean superAdmin);

    List<SoapPackage> queryAllByLimit(@Param("pkg") SoapPackage pkg,
                                      @Param("pageable") Pageable pageable,
                                      @Param("currentUserId") Long currentUserId,
                                      @Param("superAdmin") boolean superAdmin);

    long count(@Param("pkg") SoapPackage pkg,
               @Param("currentUserId") Long currentUserId,
               @Param("superAdmin") boolean superAdmin);

    int insert(SoapPackage pkg);

    int update(SoapPackage pkg);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int deleteById(Integer id);
}
