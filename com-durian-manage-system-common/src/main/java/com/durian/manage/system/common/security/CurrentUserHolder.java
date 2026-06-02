package com.durian.manage.system.common.security;

/**
 * 当前登录用户的 ThreadLocal 容器。
 * 由 JwtAuthFilter 在请求开始时注入，请求结束时清理。
 * Service 层通过静态方法获取当前用户，避免在每个方法签名里传递 userId。
 */
public final class CurrentUserHolder {

    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    private CurrentUserHolder() {
    }

    public static void set(UserContext ctx) {
        HOLDER.set(ctx);
    }

    public static UserContext get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        UserContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.getUserId();
    }

    public static String getUsername() {
        UserContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.getUsername();
    }

    public static Integer getRole() {
        UserContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.getRole();
    }

    public static boolean isSuperAdmin() {
        UserContext ctx = HOLDER.get();
        return ctx != null && ctx.isSuperAdmin();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
