package com.durian.manage.system.dao;

import com.durian.manage.system.domain.ComDurianOil;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * (ComDurianOil)表数据库访问层
 *
 * 多租户隔离：
 *  - owner_user_id = 0  → 系统默认数据，所有人可见，只有超管可改
 *  - owner_user_id = N  → 用户 N 的个人数据，只有 N 和超管可改
 *
 * @author makejava
 * @since 2024-03-21 11:29:24
 */
public interface ComDurianOilDao {

    ComDurianOil queryById(Integer id);

    List<ComDurianOil> queryAllByLimit(@Param("comDurianOil") ComDurianOil comDurianOil,
                                       @Param("pageable") Pageable pageable,
                                       @Param("currentUserId") Long currentUserId,
                                       @Param("superAdmin") boolean superAdmin);

    long count(@Param("comDurianOil") ComDurianOil comDurianOil,
               @Param("currentUserId") Long currentUserId,
               @Param("superAdmin") boolean superAdmin);

    int insert(ComDurianOil comDurianOil);

    int insertBatch(@Param("entities") List<ComDurianOil> entities);

    int insertOrUpdateBatch(@Param("entities") List<ComDurianOil> entities);

    int update(ComDurianOil comDurianOil);

    int deleteById(Integer id);

    List<ComDurianOil> queryAll(@Param("currentUserId") Long currentUserId,
                                @Param("superAdmin") boolean superAdmin);
}
