package com.durian.manage.system.web.controller;

import com.durian.manage.system.common.exception.ApiException;
import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.common.security.PermissionKeys;
import com.durian.manage.system.common.util.IpUtils;
import com.durian.manage.system.domain.User;
import com.durian.manage.system.service.UserService;
import com.durian.manage.system.service.dto.LoginPrecheckResponse;
import com.durian.manage.system.service.dto.LoginRequest;
import com.durian.manage.system.service.dto.LoginResponse;
import com.durian.manage.system.service.dto.PasswordChangeRequest;
import com.durian.manage.system.service.dto.ProfileUpdateRequest;
import com.durian.manage.system.service.dto.RegisterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req,
                                                        HttpServletRequest request) {
        String ip = IpUtils.getClientIp(request);
        User user = userService.register(req, ip);
        Map<String, Object> body = new HashMap<>();
        body.put("userId", user.getId());
        body.put("username", user.getUsername());
        body.put("message", "注册成功，请登录");
        return ResponseEntity.ok(body);
    }

    /**
     * 登录前预检：前端输完用户名后立即调用，判断是否需要弹滑块。
     */
    @GetMapping("/precheck")
    public ResponseEntity<LoginPrecheckResponse> precheck(@RequestParam String username) {
        return ResponseEntity.ok(userService.precheck(username));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req,
                                               HttpServletRequest request) {
        String ip = IpUtils.getClientIp(request);
        return ResponseEntity.ok(userService.login(req, ip));
    }

    /**
     * 当前登录用户信息：刷新页面后前端 store 重新初始化时调用。
     *  superadmin / Admin@2026
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw ApiException.unauthorized("请先登录");
        }
        User user = userService.queryById(uid);
        if (user == null) {
            throw ApiException.unauthorized("账号不存在");
        }
        return ResponseEntity.ok(buildMeBody(user));
    }

    /** 更新皂坊名 + 个人简介 */
    @PutMapping("/me/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody ProfileUpdateRequest req) {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw ApiException.unauthorized("请先登录");
        }
        User user = userService.updateProfile(uid, req.getWorkshopName(), req.getBio());
        return ResponseEntity.ok(buildMeBody(user));
    }

    /** 修改密码 */
    @PutMapping("/me/password")
    public ResponseEntity<Map<String, Object>> changePassword(@Valid @RequestBody PasswordChangeRequest req) {
        Long uid = CurrentUserHolder.getUserId();
        if (uid == null) {
            throw ApiException.unauthorized("请先登录");
        }
        userService.changePassword(uid, req.getOldPassword(), req.getNewPassword());
        Map<String, Object> body = new HashMap<>();
        body.put("message", "密码修改成功，请重新登录");
        return ResponseEntity.ok(body);
    }

    private Map<String, Object> buildMeBody(User user) {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", user.getId());
        body.put("username", user.getUsername());
        body.put("email", user.getEmail());
        body.put("workshopName", user.getWorkshopName());
        body.put("bio", user.getBio());
        body.put("role", user.getRole());
        body.put("roleName", user.getRole() == User.ROLE_SUPER_ADMIN ? "超级管理员" : "普通用户");
        body.put("permissKeys", PermissionKeys.forRole(user.getRole()));
        return body;
    }
}
