package com.durian.manage.system.web.init;

import com.durian.manage.system.dao.UserDao;
import com.durian.manage.system.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 启动时自动确保有一个超级管理员账号可用。
 *
 * - 默认账号: superadmin / Admin@2026
 * - 可通过 application.properties 覆盖:
 *     app.super-admin.username=xxx
 *     app.super-admin.password=xxx
 *     app.super-admin.auto-create=false   关闭该机制
 * - 不会重复创建；如果该用户名已存在但角色不是 super_admin，不会自动提升（避免覆盖）。
 * - 每次启动日志都会输出当前默认账号提示，方便忘记密码时查阅。
 */
@Component
public class SuperAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminInitializer.class);

    @Value("${app.super-admin.auto-create:true}")
    private boolean autoCreate;

    @Value("${app.super-admin.username:superadmin}")
    private String defaultUsername;

    @Value("${app.super-admin.password:Admin@2026}")
    private String defaultPassword;

    @Resource
    private UserDao userDao;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!autoCreate) {
            return;
        }
        try {
            User existing = userDao.queryByUsername(defaultUsername);
            if (existing == null) {
                User u = new User();
                u.setUsername(defaultUsername);
                u.setPassword(passwordEncoder.encode(defaultPassword));
                u.setEmail(null);
                u.setRole(User.ROLE_SUPER_ADMIN);
                u.setStatus(User.STATUS_ACTIVE);
                userDao.insert(u);
                printBanner(true);
            } else if (existing.getRole() != null && existing.getRole() == User.ROLE_SUPER_ADMIN) {
                printBanner(false);
            } else {
                log.warn("用户名 {} 已存在但角色不是超级管理员，跳过自动创建。", defaultUsername);
            }
        } catch (Exception e) {
            // 表不存在时给清晰提示
            log.error("超级管理员初始化失败：{}。如果是表不存在，请先执行 alter_user_module_v1.sql", e.getMessage());
        }
    }

    private void printBanner(boolean justCreated) {
        log.warn("====================================================");
        log.warn("  {} 超级管理员账号：", justCreated ? "已自动创建" : "当前可用");
        log.warn("    用户名: {}", defaultUsername);
        log.warn("    密  码: {}", defaultPassword);
        log.warn("  请登录后尽快修改密码！");
        log.warn("====================================================");
    }
}
