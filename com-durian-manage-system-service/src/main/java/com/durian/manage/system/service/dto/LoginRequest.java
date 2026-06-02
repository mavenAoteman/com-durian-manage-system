package com.durian.manage.system.service.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

    /** 登录标识：用户名或邮箱 */
    @NotBlank(message = "请输入用户名或邮箱")
    private String username;

    @NotBlank(message = "请输入密码")
    private String password;

    /**
     * 滑块验证码标记：前端通过拼图后传 true。
     * 当账号被判定需要验证码时，必须为 true 才能继续登录。
     */
    private Boolean captchaPassed;

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

    public Boolean getCaptchaPassed() {
        return captchaPassed;
    }

    public void setCaptchaPassed(Boolean captchaPassed) {
        this.captchaPassed = captchaPassed;
    }
}
