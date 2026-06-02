package com.durian.manage.system.common.security;

import java.io.Serializable;

/**
 * 当前登录用户上下文
 */
public class UserContext implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int ROLE_SUPER_ADMIN = 1;
    public static final int ROLE_USER = 2;

    private Long userId;
    private String username;
    private Integer role;

    public UserContext() {
    }

    public UserContext(Long userId, String username, Integer role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public boolean isSuperAdmin() {
        return role != null && role == ROLE_SUPER_ADMIN;
    }
}
