package com.durian.manage.system.service.dto;

import java.util.List;

/**
 * 登录成功响应
 */
public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String workshopName;
    private String bio;
    private Integer role;
    private String roleName;
    /** 前端 v-permiss 用的权限 key 列表 */
    private List<String> permissKeys;
    private long expireSeconds;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getPermissKeys() {
        return permissKeys;
    }

    public void setPermissKeys(List<String> permissKeys) {
        this.permissKeys = permissKeys;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
