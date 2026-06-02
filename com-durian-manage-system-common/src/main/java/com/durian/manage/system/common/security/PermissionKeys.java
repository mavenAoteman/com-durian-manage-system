package com.durian.manage.system.common.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 权限 key 定义。
 * 与前端 store/permiss.ts 的 FULL_KEYS 保持一致。
 * 普通用户：业务功能 1-16。
 * 超级管理员：额外拥有 user_manage。
 */
public final class PermissionKeys {

    public static final String USER_MANAGE = "user_manage";

    public static final List<String> USER_KEYS = Collections.unmodifiableList(
            Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                    "11", "12", "13", "14", "15", "16")
    );

    public static final List<String> SUPER_ADMIN_KEYS;

    static {
        java.util.List<String> all = new java.util.ArrayList<>(USER_KEYS);
        all.add(USER_MANAGE);
        SUPER_ADMIN_KEYS = Collections.unmodifiableList(all);
    }

    private PermissionKeys() {
    }

    public static List<String> forRole(Integer role) {
        if (role != null && role == UserContext.ROLE_SUPER_ADMIN) {
            return SUPER_ADMIN_KEYS;
        }
        return USER_KEYS;
    }
}
