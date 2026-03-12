package com.example.demo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers lightweight MVC security behavior for the demo API.
 */
@Configuration
public class WebMvcSecurityConfig implements WebMvcConfigurer {

    private final ApiKeyAuthInterceptor apiKeyAuthInterceptor;

    public WebMvcSecurityConfig(ApiKeyAuthInterceptor apiKeyAuthInterceptor) {
        this.apiKeyAuthInterceptor = apiKeyAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyAuthInterceptor)
                .addPathPatterns("/api/**");
    }
}
