package com.durian.manage.system.dao;

import com.durian.manage.system.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 用户表数据访问层
 *
 * @author maven12
 * @since 2026-06-01
 */
public interface UserDao {

    User queryById(Long id);

    User queryByUsername(String username);

    User queryByEmail(String email);

    /**
     * 登录用：先按 username 精确匹配，找不到再按 email 匹配。
     */
    User queryByLoginIdentifier(@Param("identifier") String identifier);

    int insert(User user);

    /**
     * 登录成功后更新：清零失败次数 + 记录登录时间/IP
     */
    int updateLoginSuccess(@Param("id") Long id,
                           @Param("lastLoginAt") Date lastLoginAt,
                           @Param("lastLoginIp") String lastLoginIp);

    /**
     * 登录失败后更新失败次数；达到阈值时设置 lockedUntil
     */
    int updateLoginFailure(@Param("id") Long id,
                           @Param("failedLoginCount") int failedLoginCount,
                           @Param("lockedUntil") Date lockedUntil);

    /**
     * 修改密码（密码已 BCrypt 哈希）
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 修改个人资料：皂坊名 + 简介
     */
    int updateProfile(@Param("id") Long id,
                      @Param("workshopName") String workshopName,
                      @Param("bio") String bio);
}
