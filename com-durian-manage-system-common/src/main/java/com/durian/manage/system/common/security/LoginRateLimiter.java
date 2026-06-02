package com.durian.manage.system.common.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录/注册的限流、账号锁定、IP 黑名单。
 * 全部基于 Caffeine 内存缓存，重启后清空。
 * 单机部署够用，多实例部署需切换到 Redis。
 */
@Component
public class LoginRateLimiter {

    /** 单账号失败 N 次后锁定（默认 5） */
    public static final int MAX_LOGIN_FAILURES = 5;
    /** 单账号超过 N 次失败后强制弹滑块（默认 2） */
    public static final int CAPTCHA_THRESHOLD = 2;
    /** 锁定时长（分钟） */
    public static final int LOCK_MINUTES = 15;
    /** 单 IP 每分钟最多登录请求次数 */
    public static final int IP_LOGIN_LIMIT_PER_MINUTE = 10;
    /** 单 IP 每小时最多注册次数 */
    public static final int IP_REGISTER_LIMIT_PER_HOUR = 3;

    /** 账号失败计数，15 分钟后自动清零（成功登录也会清零） */
    private final Cache<String, AtomicInteger> usernameFailures = Caffeine.newBuilder()
            .expireAfterWrite(LOCK_MINUTES, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /** 单 IP 1 分钟登录请求计数 */
    private final Cache<String, AtomicInteger> ipLoginAttempts = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /** 单 IP 1 小时注册计数 */
    private final Cache<String, AtomicInteger> ipRegisterAttempts = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();

    /** IP 黑名单，24 小时自动解封 */
    private final Cache<String, Boolean> ipBlacklist = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();

    /** IP 是否在黑名单中 */
    public boolean isIpBlacklisted(String ip) {
        return Boolean.TRUE.equals(ipBlacklist.getIfPresent(ip));
    }

    public void blockIp(String ip) {
        ipBlacklist.put(ip, Boolean.TRUE);
    }

    public void unblockIp(String ip) {
        ipBlacklist.invalidate(ip);
    }

    /**
     * 每次登录请求调用一次，超出阈值返回 true。
     */
    public boolean isLoginIpRateLimited(String ip) {
        AtomicInteger counter = ipLoginAttempts.get(ip, k -> new AtomicInteger(0));
        return counter.incrementAndGet() > IP_LOGIN_LIMIT_PER_MINUTE;
    }

    public boolean isRegisterIpRateLimited(String ip) {
        AtomicInteger counter = ipRegisterAttempts.get(ip, k -> new AtomicInteger(0));
        return counter.incrementAndGet() > IP_REGISTER_LIMIT_PER_HOUR;
    }

    /** 登录失败 +1，返回当前失败次数 */
    public int recordLoginFailure(String username) {
        AtomicInteger counter = usernameFailures.get(username, k -> new AtomicInteger(0));
        return counter.incrementAndGet();
    }

    /** 当前失败次数（不增加） */
    public int getLoginFailureCount(String username) {
        AtomicInteger counter = usernameFailures.getIfPresent(username);
        return counter == null ? 0 : counter.get();
    }

    /** 登录成功后清零 */
    public void clearLoginFailures(String username) {
        usernameFailures.invalidate(username);
    }

    /** 是否需要弹滑块（失败次数 >= 阈值） */
    public boolean needsCaptcha(String username) {
        return getLoginFailureCount(username) >= CAPTCHA_THRESHOLD;
    }
}
