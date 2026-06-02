package com.durian.manage.system.service.dto;

import javax.validation.constraints.NotBlank;

public class PasswordChangeRequest {

    @NotBlank(message = "请输入旧密码")
    private String oldPassword;

    @NotBlank(message = "请输入新密码")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
