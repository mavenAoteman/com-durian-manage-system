package com.durian.manage.system.dao;

import com.durian.manage.system.domain.SoapPackageMaterial;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SoapPackageMaterialDao {
    List<SoapPackageMaterial> queryByPackageId(@Param("packageId") Integer packageId);
    int insert(SoapPackageMaterial m);
    int insertBatch(@Param("entities") List<SoapPackageMaterial> entities);
    int deleteByPackageId(@Param("packageId") Integer packageId);
}
