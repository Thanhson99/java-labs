package com.example.demo.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * External configuration for registration rate limiting.
 */
@ConfigurationProperties(prefix = "app.rate-limit.registration")
public record RegistrationRateLimitProperties(int maxRequests, long windowMillis) {
}
