package com.durian.manage.system.web.filter;

/**
 * @author maven12
 * @className RequestFilter
 * @description filter
 * @date 2024/11/19 09:39
 **/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 解决跨域设置
 * （可把此设置放在 nginx 中，但只能设置一处）
 */

@Component
@Order(1)
@WebFilter(filterName = "requestFilter", urlPatterns = {"/*"})
public class RequestFilter implements Filter {
    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(RequestFilter.class);

    /**
     * 默认日志id标识
     */
    public static final String LOG_ID = "logid";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            MDC.put(LOG_ID,String.valueOf(UUID.randomUUID()).replace("-",""));
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            // 此处 setHeader、addHeader 方法都可用。但 addHeader时写多个会报错：“...,but only one is allowed”
            response.setHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
            // 解决预请求（发送2次请求），此问题也可在 nginx 中作相似设置解决。
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Cache-Control,Pragma,Content-Type,Token,Authorization");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            String method = request.getMethod();
            if (method.equalsIgnoreCase("OPTIONS")) {
                servletResponse.getOutputStream().write("Success".getBytes(StandardCharsets.UTF_8));
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } finally {
            MDC.remove(LOG_ID);
        }
    }

    @Override
    public void destroy() {

    }

}