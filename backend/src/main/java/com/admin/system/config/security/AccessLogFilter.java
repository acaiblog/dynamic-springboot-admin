package com.admin.system.config.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * API访问日志过滤器
 * 记录所有HTTP请求的访问信息：方法、URI、响应状态码、耗时、用户、IP
 */
@Component
public class AccessLogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    // 需要排除的路径
    private static final String[] EXCLUDE_PATHS = {
        "/auth/login",
        "/h2-console",
        "/doc.html",
        "/swagger-ui",
        "/v3/api-docs"
    };

    @PostConstruct
    public void init() {
        log.info("✅ AccessLogFilter 已初始化，所有API请求将被记录");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // 排除不需要记录的路径
        if (shouldExclude(requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 直接传递，不包装body
        chain.doFilter(request, response);

        // 计算耗时
        long duration = System.currentTimeMillis() - startTime;

        // 获取状态码
        int status = httpResponse.getStatus();

        // 获取用户名
        String username = getCurrentUsername();

        // 输出访问日志
        log.info("📢 [API访问] {} {} | 状态:{} | 耗时:{}ms | 用户:{}",
                method, requestUri, status, duration, username);
    }

    private boolean shouldExclude(String uri) {
        for (String path : EXCLUDE_PATHS) {
            if (uri.contains(path)) {
                return true;
            }
        }
        return false;
    }

    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // SecurityContext 可能尚未初始化
        }
        return "anonymous";
    }
}
