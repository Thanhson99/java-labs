package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Very small API key filter used to protect demo API endpoints.
 *
 * <p>This is not a full production-ready security design, but it is a useful teaching step before
 * moving to OAuth2, JWT, or more advanced Spring Security setups.</p>
 */
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyAuthProperties properties;

    public ApiKeyAuthenticationFilter(ApiKeyAuthProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String actualKey = request.getHeader(properties.headerName());
        if (!properties.value().equals(actualKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"invalid API key\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
