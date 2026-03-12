package com.example.demo.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration for simple API key authentication.
 */
@ConfigurationProperties(prefix = "app.security.api-key")
public record ApiKeyAuthProperties(String headerName, String value) {
}
