package com.durian.manage.system.service;

import com.durian.manage.system.domain.User;
import com.durian.manage.system.service.dto.LoginPrecheckResponse;
import com.durian.manage.system.service.dto.LoginRequest;
import com.durian.manage.system.service.dto.LoginResponse;
import com.durian.manage.system.service.dto.RegisterRequest;

public interface UserService {

    /**
     * 注册：必须通过滑块；同 IP 一小时限 3 次；用户名唯一；返回创建后的 User（不含密码）。
     */
    User register(RegisterRequest req, String clientIp);

    /**
     * 登录预检：前端把用户名发过来，后端返回是否需要弹滑块。
     */
    LoginPrecheckResponse precheck(String username);

    /**
     * 登录：IP 限流 → 账号锁定校验 → 必要时验证滑块 → 密码校验 → 签发 JWT。
     */
    LoginResponse login(LoginRequest req, String clientIp);

    /**
     * 获取当前登录用户（不含密码）
     */
    User queryById(Long id);

    /**
     * 更新个人资料：皂坊名 + 简介
     */
    User updateProfile(Long userId, String workshopName, String bio);

    /**
     * 修改密码：校验旧密码 + 新密码强度
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
