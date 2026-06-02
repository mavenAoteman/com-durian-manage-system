package com.durian.manage.system.web.filter;

import com.durian.manage.system.common.security.CurrentUserHolder;
import com.durian.manage.system.common.security.JwtUtil;
import com.durian.manage.system.common.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 鉴权过滤器：解析 Authorization: Bearer {jwt}，注入 CurrentUserHolder。
 * 白名单接口直接放行；OPTIONS 预检放行；其他接口缺少/过期 token 返回 401 JSON。
 * Order(2)：必须在 RequestFilter(Order 1) 之后执行，保证 401 响应也带 CORS 头。
 */
@Component
@Order(2)
public class JwtAuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    /** 无需登录就能访问的路径前缀 */
    private static final Set<String> WHITELIST = new HashSet<>(Arrays.asList(
            "/auth/login",
            "/auth/register"
    ));

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // OPTIONS 预检放行（RequestFilter 已处理）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, resp);
            return;
        }

        String path = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }

        if (isWhitelisted(path)) {
            chain.doFilter(req, resp);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[JwtAuth] 拒绝访问 {} {} 原因=未携带 Bearer token, Authorization 头={}",
                    request.getMethod(), path, authHeader);
            writeUnauthorized(response, "请先登录");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        try {
            UserContext userContext = jwtUtil.parse(token);
            CurrentUserHolder.set(userContext);
            log.debug("[JwtAuth] {} {} 通过, uid={} role={}",
                    request.getMethod(), path, userContext.getUserId(), userContext.getRole());
            chain.doFilter(req, resp);
        } catch (Exception e) {
            log.warn("[JwtAuth] 拒绝访问 {} {} 原因=JWT 解析失败: {}",
                    request.getMethod(), path, e.getMessage());
            writeUnauthorized(response, "登录已过期，请重新登录");
        } finally {
            CurrentUserHolder.clear();
        }
    }

    private boolean isWhitelisted(String path) {
        if (path == null) {
            return false;
        }
        for (String w : WHITELIST) {
            if (path.equals(w) || path.startsWith(w + "/")) {
                return true;
            }
        }
        return false;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String body = "{\"code\":401,\"message\":\"" + message + "\"}";
        response.getWriter().write(body);
        response.getWriter().flush();
    }

    @Override
    public void destroy() {
    }
}
