package com.example.demo.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration values for the demo JWT security layer.
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String secret, long expirationSeconds) {
}
