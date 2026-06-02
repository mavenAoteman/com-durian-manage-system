package com.durian.manage.system.dao;

import com.durian.manage.system.domain.SoapPiece;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 皂块分装(SoapPiece)表数据库访问层
 *
 * @author maven12
 * @since 2026-05-01
 */
public interface SoapPieceDao {

    SoapPiece queryById(Integer id);

    List<SoapPiece> queryByProductionId(@Param("productionId") Integer productionId);

    int insert(SoapPiece soapPiece);

    int insertBatch(@Param("entities") List<SoapPiece> entities);

    int update(SoapPiece soapPiece);

    int deleteById(Integer id);

    int deleteByProductionId(@Param("productionId") Integer productionId);

    List<SoapPiece> queryAll(@Param("currentUserId") Long currentUserId,
                             @Param("superAdmin") boolean superAdmin);

    List<SoapPiece> queryByStatus(@Param("status") Integer status,
                                  @Param("currentUserId") Long currentUserId,
                                  @Param("superAdmin") boolean superAdmin);
}
