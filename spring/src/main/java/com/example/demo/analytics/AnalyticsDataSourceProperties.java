package com.example.demo.analytics;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Secondary datasource settings used for analytics-style writes.
 */
@ConfigurationProperties(prefix = "app.analytics.datasource")
public record AnalyticsDataSourceProperties(
        String url,
        String username,
        String password,
        String driverClassName
) {
}
