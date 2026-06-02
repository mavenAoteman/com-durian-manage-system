package com.durian.manage.system.common.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 取客户端 IP，兼顾 nginx / 代理转发。
 */
public final class IpUtils {

    private IpUtils() {
    }

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (isValid(ip)) {
            // 取第一个（最原始的客户端 IP）
            int comma = ip.indexOf(',');
            return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (isValid(ip)) {
            return ip.trim();
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (isValid(ip)) {
            return ip.trim();
        }
        ip = request.getRemoteAddr();
        return ip == null ? "unknown" : ip;
    }

    private static boolean isValid(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}
