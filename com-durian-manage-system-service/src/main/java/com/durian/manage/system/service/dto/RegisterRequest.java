package com.durian.manage.system.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class RegisterRequest {

    /** 用户名：3-20 位字母数字下划线，或合法邮箱 */
    @NotBlank(message = "请输入用户名")
    @Pattern(
        regexp = "^([a-zA-Z0-9_]{3,20})$|^([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$",
        message = "用户名需 3-20 位字母/数字/下划线，或合法邮箱"
    )
    private String username;

    /** 密码强度由 PasswordValidator 在 service 层判 */
    @NotBlank(message = "请输入密码")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    /** 滑块验证码标记：注册必须为 true */
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getCaptchaPassed() {
        return captchaPassed;
    }

    public void setCaptchaPassed(Boolean captchaPassed) {
        this.captchaPassed = captchaPassed;
    }
}
