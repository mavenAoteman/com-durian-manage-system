package com.durian.manage.system.common.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 密码强度校验：
 * 1. 长度 8-32
 * 2. 必须同时包含字母和数字
 * 3. 不能命中弱密码黑名单
 * 4. 不能与用户名相同
 */
public final class PasswordValidator {

    private static final Pattern PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[\\x21-\\x7e]{8,32}$");

    /** 常见弱密码黑名单（不区分大小写） */
    private static final Set<String> WEAK = new HashSet<>(Arrays.asList(
            "12345678", "123456789", "1234567890",
            "password", "password1", "passw0rd",
            "qwerty", "qwerty123", "asdf1234", "abc12345", "abcd1234",
            "11111111", "00000000", "88888888", "66666666",
            "iloveyou", "welcome1", "admin123", "admin1234",
            "1qaz2wsx", "1q2w3e4r", "1q2w3e4r5t",
            "letmein123", "monkey123", "dragon123"
    ));

    private PasswordValidator() {
    }

    /**
     * @return null 表示通过；非 null 是错误消息（直接给用户看）
     */
    public static String validate(String password, String username) {
        if (password == null || password.isEmpty()) {
            return "请输入密码";
        }
        if (password.length() < 8 || password.length() > 32) {
            return "密码长度需 8-32 位";
        }
        if (!PATTERN.matcher(password).matches()) {
            return "密码必须同时包含字母和数字";
        }
        if (WEAK.contains(password.toLowerCase())) {
            return "密码过于简单，请换一个更复杂的密码";
        }
        if (username != null && password.equalsIgnoreCase(username)) {
            return "密码不能与用户名相同";
        }
        return null;
    }
}
