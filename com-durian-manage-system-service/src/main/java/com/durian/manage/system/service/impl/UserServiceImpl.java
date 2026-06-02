package com.durian.manage.system.service.impl;

import com.durian.manage.system.common.exception.ApiException;
import com.durian.manage.system.common.security.JwtUtil;
import com.durian.manage.system.common.security.LoginRateLimiter;
import com.durian.manage.system.common.security.PasswordValidator;
import com.durian.manage.system.common.security.PermissionKeys;
import com.durian.manage.system.dao.UserDao;
import com.durian.manage.system.domain.User;
import com.durian.manage.system.service.UserService;
import com.durian.manage.system.service.dto.LoginPrecheckResponse;
import com.durian.manage.system.service.dto.LoginRequest;
import com.durian.manage.system.service.dto.LoginResponse;
import com.durian.manage.system.service.dto.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserDao userDao;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private LoginRateLimiter rateLimiter;

    @Override
    public User register(RegisterRequest req, String clientIp) {
        if (req.getCaptchaPassed() == null || !req.getCaptchaPassed()) {
            throw ApiException.badRequest("请先通过滑块验证");
        }
        if (rateLimiter.isIpBlacklisted(clientIp)) {
            throw ApiException.forbidden("您的 IP 已被封禁");
        }
        if (rateLimiter.isRegisterIpRateLimited(clientIp)) {
            throw ApiException.tooManyRequests("注册过于频繁，请稍后再试");
        }

        String pwdErr = PasswordValidator.validate(req.getPassword(), req.getUsername());
        if (pwdErr != null) {
            throw ApiException.badRequest(pwdErr);
        }

        if (userDao.queryByUsername(req.getUsername()) != null) {
            throw ApiException.badRequest("用户名已被占用");
        }
        if (req.getEmail() != null && !req.getEmail().isEmpty()
                && userDao.queryByEmail(req.getEmail()) != null) {
            throw ApiException.badRequest("邮箱已被占用");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setWorkshopName(req.getUsername() + "的皂坊");
        user.setRole(User.ROLE_USER);
        user.setStatus(User.STATUS_ACTIVE);
        userDao.insert(user);

        log.info("用户注册成功 userId={} username={} ip={}", user.getId(), user.getUsername(), clientIp);
        user.setPassword(null);
        return user;
    }

    @Override
    public LoginPrecheckResponse precheck(String username) {
        if (username == null || username.isEmpty()) {
            return new LoginPrecheckResponse(false);
        }
        return new LoginPrecheckResponse(rateLimiter.needsCaptcha(username));
    }

    @Override
    public LoginResponse login(LoginRequest req, String clientIp) {
        if (rateLimiter.isIpBlacklisted(clientIp)) {
            throw ApiException.forbidden("您的 IP 已被封禁");
        }
        if (rateLimiter.isLoginIpRateLimited(clientIp)) {
            throw ApiException.tooManyRequests("登录过于频繁，请稍后再试");
        }

        boolean needCaptcha = rateLimiter.needsCaptcha(req.getUsername());
        if (needCaptcha && (req.getCaptchaPassed() == null || !req.getCaptchaPassed())) {
            throw new ApiException(400, 1001, "请先通过滑块验证");
        }

        User user = userDao.queryByLoginIdentifier(req.getUsername());
        // 用户名不存在和密码错误返回相同提示，避免用户名枚举
        if (user == null) {
            int failCount = rateLimiter.recordLoginFailure(req.getUsername());
            log.info("登录失败 用户名不存在 username={} ip={} count={}", req.getUsername(), clientIp, failCount);
            throw ApiException.badRequest(buildFailMessage(failCount));
        }

        if (user.getStatus() != null && user.getStatus() == User.STATUS_DISABLED) {
            throw ApiException.forbidden("账号已被禁用，请联系管理员");
        }

        Date now = new Date();
        if (user.getLockedUntil() != null && user.getLockedUntil().after(now)) {
            long remainMin = (user.getLockedUntil().getTime() - now.getTime()) / 60_000 + 1;
            throw ApiException.locked("账号已锁定，请 " + remainMin + " 分钟后再试");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            int failCount = rateLimiter.recordLoginFailure(req.getUsername());
            Date lockedUntil = null;
            if (failCount >= LoginRateLimiter.MAX_LOGIN_FAILURES) {
                lockedUntil = new Date(now.getTime() + LoginRateLimiter.LOCK_MINUTES * 60_000L);
            }
            userDao.updateLoginFailure(user.getId(), failCount, lockedUntil);
            log.info("登录失败 密码错误 userId={} username={} count={} locked={}",
                    user.getId(), user.getUsername(), failCount, lockedUntil);
            if (lockedUntil != null) {
                throw ApiException.locked("密码错误次数过多，账号已锁定 "
                        + LoginRateLimiter.LOCK_MINUTES + " 分钟");
            }
            throw ApiException.badRequest(buildFailMessage(failCount));
        }

        rateLimiter.clearLoginFailures(req.getUsername());
        userDao.updateLoginSuccess(user.getId(), now, clientIp);

        String token = jwtUtil.issue(user.getId(), user.getUsername(), user.getRole());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setWorkshopName(user.getWorkshopName());
        resp.setBio(user.getBio());
        resp.setRole(user.getRole());
        resp.setRoleName(user.getRole() == User.ROLE_SUPER_ADMIN ? "超级管理员" : "普通用户");
        resp.setPermissKeys(PermissionKeys.forRole(user.getRole()));
        resp.setExpireSeconds(jwtUtil.getExpireSeconds());
        log.info("登录成功 userId={} username={} ip={}", user.getId(), user.getUsername(), clientIp);
        return resp;
    }

    private String buildFailMessage(int failCount) {
        int remain = LoginRateLimiter.MAX_LOGIN_FAILURES - failCount;
        if (remain <= 0) {
            return "用户名或密码错误";
        }
        return "用户名或密码错误，剩余 " + remain + " 次机会";
    }

    @Override
    public User queryById(Long id) {
        User u = userDao.queryById(id);
        if (u != null) {
            u.setPassword(null);
        }
        return u;
    }

    @Override
    public User updateProfile(Long userId, String workshopName, String bio) {
        if (userId == null) {
            throw ApiException.unauthorized("请先登录");
        }
        User user = userDao.queryById(userId);
        if (user == null) {
            throw ApiException.unauthorized("账号不存在");
        }
        String name = workshopName == null ? "" : workshopName.trim();
        if (name.isEmpty()) {
            // 留空则恢复默认
            name = user.getUsername() + "的皂坊";
        }
        if (name.length() > 100) {
            throw ApiException.badRequest("皂坊名最多 100 个字符");
        }
        String b = bio == null ? null : bio.trim();
        if (b != null && b.length() > 500) {
            throw ApiException.badRequest("个人简介最多 500 个字符");
        }
        userDao.updateProfile(userId, name, b);
        log.info("用户资料更新 userId={} workshopName={}", userId, name);
        return queryById(userId);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null) {
            throw ApiException.unauthorized("请先登录");
        }
        if (oldPassword == null || oldPassword.isEmpty()) {
            throw ApiException.badRequest("请输入旧密码");
        }
        User user = userDao.queryById(userId);
        if (user == null) {
            throw ApiException.unauthorized("账号不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw ApiException.badRequest("旧密码错误");
        }
        String err = PasswordValidator.validate(newPassword, user.getUsername());
        if (err != null) {
            throw ApiException.badRequest(err);
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw ApiException.badRequest("新密码不能与旧密码相同");
        }
        userDao.updatePassword(userId, passwordEncoder.encode(newPassword));
        log.info("用户修改密码成功 userId={}", userId);
    }
}
