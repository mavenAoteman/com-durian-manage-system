package com.durian.manage.system.dao;

import com.durian.manage.system.domain.SoapPackagePiece;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SoapPackagePieceDao {
    List<SoapPackagePiece> queryByPackageId(@Param("packageId") Integer packageId);
    int insert(SoapPackagePiece piece);
    int insertBatch(@Param("entities") List<SoapPackagePiece> entities);
    int deleteByPackageId(@Param("packageId") Integer packageId);
    /** 从指定打包批次中移出指定皂块（仅删除关联记录，不删皂块本身） */
    int deleteByPackageIdAndPieceIds(@Param("packageId") Integer packageId,
                                     @Param("pieceIds") List<Integer> pieceIds);
    /** 返回这批 pieceId 中，被任何打包记录引用的子集（用于删除前 FK 保护） */
    List<Integer> queryReferencedPieceIds(@Param("pieceIds") List<Integer> pieceIds);
}
