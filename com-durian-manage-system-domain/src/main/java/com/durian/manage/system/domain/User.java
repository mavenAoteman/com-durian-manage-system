package com.durian.manage.system.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表实体
 *
 * @author maven12
 * @since 2026-06-01
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色：1=超级管理员 */
    public static final int ROLE_SUPER_ADMIN = 1;
    /** 角色：2=普通用户 */
    public static final int ROLE_USER = 2;

    /** 状态：1=正常 */
    public static final int STATUS_ACTIVE = 1;
    /** 状态：2=禁用 */
    public static final int STATUS_DISABLED = 2;

    private Long id;
    private String username;
    /** BCrypt 哈希后的密码，前端永远拿不到 */
    private String password;
    private String email;
    /** 皂坊名称，默认为"用户名+的皂坊"，用户可在个人中心修改 */
    private String workshopName;
    /** 个人简介 */
    private String bio;
    private Integer role;
    private Integer status;
    private Integer failedLoginCount;
    private Date lockedUntil;
    private Date lastLoginAt;
    private String lastLoginIp;
    private Date created;
    private Date modified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getFailedLoginCount() {
        return failedLoginCount;
    }

    public void setFailedLoginCount(Integer failedLoginCount) {
        this.failedLoginCount = failedLoginCount;
    }

    public Date getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Date lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
