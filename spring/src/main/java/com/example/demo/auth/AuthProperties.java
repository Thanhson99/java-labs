package com.example.demo.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Authentication timing configuration for access and refresh tokens.
 */
@ConfigurationProperties(prefix = "app.security.auth")
public record AuthProperties(long refreshExpirationSeconds) {
}
