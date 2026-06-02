package com.durian.manage.system.service.dto;

/**
 * 登录前的预检：前端拿用户名问后端"这个账号现在要不要弹滑块"。
 */
public class LoginPrecheckResponse {

    private boolean needCaptcha;

    public LoginPrecheckResponse(boolean needCaptcha) {
        this.needCaptcha = needCaptcha;
    }

    public boolean isNeedCaptcha() {
        return needCaptcha;
    }

    public void setNeedCaptcha(boolean needCaptcha) {
        this.needCaptcha = needCaptcha;
    }
}
