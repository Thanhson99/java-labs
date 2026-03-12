package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * MVC interceptor that protects `/api/**` endpoints with a simple API key check.
 */
@Component
public class ApiKeyAuthInterceptor implements HandlerInterceptor {

    private final ApiKeyAuthProperties properties;

    public ApiKeyAuthInterceptor(ApiKeyAuthProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String actualKey = request.getHeader(properties.headerName());
        if (!properties.value().equals(actualKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"invalid API key\"}");
            return false;
        }
        return true;
    }
}
