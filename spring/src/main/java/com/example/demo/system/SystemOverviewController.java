package com.example.demo.system;

import com.example.demo.analytics.AnalyticsEventStore;
import com.example.demo.analytics.AnalyticsDataSourceProperties;
import com.example.demo.auth.RefreshTokenStore;
import com.example.demo.messaging.RegistrationMessagingProperties;
import com.example.demo.messaging.EventConsumptionTracker;
import com.example.demo.messaging.OutboxEventStore;
import com.example.demo.messaging.UserRegistrationEventPublisher;
import com.example.demo.observability.ApplicationMetrics;
import com.example.demo.ratelimit.FixedWindowRateLimiter;
import com.example.demo.ratelimit.RegistrationRateLimitProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Small diagnostics endpoint that explains the learning architecture.
 */
@RestController
@RequestMapping("/api/system")
public class SystemOverviewController {

    private final DataSource primaryDataSource;
    private final DataSource analyticsDataSource;
    private final RegistrationRateLimitProperties rateLimitProperties;
    private final AnalyticsDataSourceProperties analyticsDataSourceProperties;
    private final AnalyticsEventStore analyticsEventStore;
    private final FixedWindowRateLimiter rateLimiter;
    private final int configuredPrimaryMaxPoolSize;
    private final RegistrationMessagingProperties messagingProperties;
    private final UserRegistrationEventPublisher eventPublisher;
    private final EventConsumptionTracker eventConsumptionTracker;
    private final String applicationName;
    private final ApplicationMetrics applicationMetrics;
    private final OutboxEventStore outboxEventStore;
    private final RefreshTokenStore refreshTokenStore;

    public SystemOverviewController(
            @Qualifier("dataSource") DataSource primaryDataSource,
            @Qualifier("analyticsDataSource") DataSource analyticsDataSource,
            RegistrationRateLimitProperties rateLimitProperties,
            AnalyticsDataSourceProperties analyticsDataSourceProperties,
            AnalyticsEventStore analyticsEventStore,
            FixedWindowRateLimiter rateLimiter,
            RegistrationMessagingProperties messagingProperties,
            UserRegistrationEventPublisher eventPublisher,
            EventConsumptionTracker eventConsumptionTracker,
            @Value("${spring.datasource.hikari.maximum-pool-size:10}") int configuredPrimaryMaxPoolSize,
            @Value("${spring.application.name:spring}") String applicationName,
            ApplicationMetrics applicationMetrics,
            OutboxEventStore outboxEventStore,
            RefreshTokenStore refreshTokenStore) {
        this.primaryDataSource = primaryDataSource;
        this.analyticsDataSource = analyticsDataSource;
        this.rateLimitProperties = rateLimitProperties;
        this.analyticsDataSourceProperties = analyticsDataSourceProperties;
        this.analyticsEventStore = analyticsEventStore;
        this.rateLimiter = rateLimiter;
        this.messagingProperties = messagingProperties;
        this.eventPublisher = eventPublisher;
        this.eventConsumptionTracker = eventConsumptionTracker;
        this.configuredPrimaryMaxPoolSize = configuredPrimaryMaxPoolSize;
        this.applicationName = applicationName;
        this.applicationMetrics = applicationMetrics;
        this.outboxEventStore = outboxEventStore;
        this.refreshTokenStore = refreshTokenStore;
    }

    @GetMapping(value = "/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> overview() {
        HikariDataSource primary = (HikariDataSource) primaryDataSource;
        HikariDataSource analytics = (HikariDataSource) analyticsDataSource;

        return Map.of(
                "primaryDatabase", Map.of(
                        "jdbcUrl", primary.getJdbcUrl(),
                        "maximumPoolSize", configuredPrimaryMaxPoolSize,
                        "runtimeMaximumPoolSize", primary.getMaximumPoolSize()
                ),
                "analyticsDatabase", Map.of(
                        "jdbcUrl", analyticsDataSourceProperties.url(),
                        "maximumPoolSize", analytics.getMaximumPoolSize(),
                        "eventCount", analyticsEventStore.countEvents()
                ),
                "registrationRateLimit", Map.of(
                        "maxRequests", rateLimitProperties.maxRequests(),
                        "windowMillis", rateLimitProperties.windowMillis(),
                        "demoRemainingForDefaultCaller", rateLimiter.remainingRequests("demo-caller")
                ),
                "messaging", Map.ofEntries(
                        Map.entry("kafkaEnabled", messagingProperties.kafka().enabled()),
                        Map.entry("rabbitmqEnabled", messagingProperties.rabbitmq().enabled()),
                        Map.entry("kafkaTopic", messagingProperties.kafka().topic()),
                        Map.entry("kafkaDltTopic", messagingProperties.kafka().deadLetterTopic()),
                        Map.entry("rabbitmqExchange", messagingProperties.rabbitmq().exchange()),
                        Map.entry("rabbitmqQueue", messagingProperties.rabbitmq().queue()),
                        Map.entry("rabbitmqDeadLetterQueue", messagingProperties.rabbitmq().deadLetterQueue()),
                        Map.entry("rabbitmqRoutingKey", messagingProperties.rabbitmq().routingKey()),
                        Map.entry("enabledTransports", eventPublisher.enabledTransports()),
                        Map.entry("consumedCounts", eventConsumptionTracker.consumedCounts()),
                        Map.entry("lastConsumedUserIds", eventConsumptionTracker.lastConsumedUserIds())
                ),
                "auth", Map.of(
                        "activeRefreshSessions", refreshTokenStore.activeTokenCount()
                ),
                "architecture", "single Spring Boot app with primary user DB, secondary analytics DB, and service boundaries that mirror a microservice design"
        );
    }

    @GetMapping(value = "/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> dashboard() {
        HikariDataSource primary = (HikariDataSource) primaryDataSource;
        HikariDataSource analytics = (HikariDataSource) analyticsDataSource;

        return Map.of(
                "application", Map.of(
                        "name", applicationName,
                        "architecture", "monolith with microservice-style boundaries",
                        "securityMode", "JWT access tokens with refresh rotation"
                ),
                "primaryDatabase", Map.of(
                        "engine", jdbcEngine(primary.getJdbcUrl()),
                        "maximumPoolSize", configuredPrimaryMaxPoolSize
                ),
                "analyticsDatabase", Map.of(
                        "engine", jdbcEngine(analyticsDataSourceProperties.url()),
                        "maximumPoolSize", analytics.getMaximumPoolSize(),
                        "eventCount", analyticsEventStore.countEvents()
                ),
                "registrationRateLimit", Map.of(
                        "maxRequests", rateLimitProperties.maxRequests(),
                        "windowMillis", rateLimitProperties.windowMillis()
                ),
                "messaging", Map.of(
                        "kafkaEnabled", messagingProperties.kafka().enabled(),
                        "rabbitmqEnabled", messagingProperties.rabbitmq().enabled(),
                        "activeTransports", eventPublisher.enabledTransports(),
                        "deadLetterPaths", java.util.List.of(
                                "Kafka -> " + messagingProperties.kafka().deadLetterTopic(),
                                "RabbitMQ -> " + messagingProperties.rabbitmq().deadLetterQueue()
                        )
                ),
                "outbox", Map.of(
                        "pending", outboxEventStore.countPending(),
                        "published", outboxEventStore.countPublished(),
                        "deadLetter", outboxEventStore.countDeadLetter()
                ),
                "auth", Map.of(
                        "activeRefreshSessions", refreshTokenStore.activeTokenCount()
                ),
                "observability", Map.of(
                        "healthEndpoint", "/actuator/health",
                        "infoEndpoint", "/actuator/info",
                        "metricsEndpoint", "/actuator/metrics",
                        "businessMetrics", applicationMetrics.snapshot()
                )
        );
    }

    private String jdbcEngine(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return "unknown";
        }
        if (jdbcUrl.startsWith("jdbc:h2:")) {
            return "H2";
        }
        if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            return "PostgreSQL";
        }
        return jdbcUrl;
    }
}
