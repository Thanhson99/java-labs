package com.example.demo.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central place for business-oriented metrics so the learning project can expose
 * more than raw infrastructure telemetry.
 */
@Component
public class ApplicationMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter authTokenIssuedCounter;
    private final Counter authRefreshSuccessCounter;
    private final Counter authLogoutSuccessCounter;
    private final Counter authLogoutAllSuccessCounter;
    private final Counter registrationSuccessCounter;
    private final Counter registrationRateLimitedCounter;
    private final Counter registrationFailureCounter;
    private final Counter outboxPublishedCounter;
    private final Counter outboxRetryCounter;
    private final Counter outboxDeadLetterCounter;
    private final Timer registrationTimer;
    private final ConcurrentHashMap<String, Counter> publishedEventCounters = new ConcurrentHashMap<>();

    public ApplicationMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.authTokenIssuedCounter = meterRegistry.counter("app.auth.tokens.issued");
        this.authRefreshSuccessCounter = meterRegistry.counter("app.auth.refresh.success");
        this.authLogoutSuccessCounter = meterRegistry.counter("app.auth.logout.success");
        this.authLogoutAllSuccessCounter = meterRegistry.counter("app.auth.logout_all.success");
        this.registrationSuccessCounter = meterRegistry.counter("app.registration.success");
        this.registrationRateLimitedCounter = meterRegistry.counter("app.registration.rate_limited");
        this.registrationFailureCounter = meterRegistry.counter("app.registration.failure");
        this.outboxPublishedCounter = meterRegistry.counter("app.outbox.published");
        this.outboxRetryCounter = meterRegistry.counter("app.outbox.retry");
        this.outboxDeadLetterCounter = meterRegistry.counter("app.outbox.dead_letter");
        this.registrationTimer = meterRegistry.timer("app.registration.duration");
    }

    public void recordTokenIssued() {
        authTokenIssuedCounter.increment();
    }

    public void recordRefreshSuccess() {
        authRefreshSuccessCounter.increment();
    }

    public void recordLogoutSuccess() {
        authLogoutSuccessCounter.increment();
    }

    public void recordLogoutAllSuccess() {
        authLogoutAllSuccessCounter.increment();
    }

    public void recordRegistrationSuccess() {
        registrationSuccessCounter.increment();
    }

    public void recordRegistrationRateLimited() {
        registrationRateLimitedCounter.increment();
    }

    public void recordRegistrationFailure() {
        registrationFailureCounter.increment();
    }

    public void recordRegistrationDuration(Duration duration) {
        registrationTimer.record(duration);
    }

    public void recordOutboxPublished() {
        outboxPublishedCounter.increment();
    }

    public void recordOutboxRetry() {
        outboxRetryCounter.increment();
    }

    public void recordOutboxDeadLetter() {
        outboxDeadLetterCounter.increment();
    }

    public void recordPublishedEvent(String transport) {
        publishedEventCounters.computeIfAbsent(
                        transport,
                        key -> Counter.builder("app.messaging.events.published")
                                .tag("transport", key)
                                .register(meterRegistry))
                .increment();
    }

    public Map<String, Object> snapshot() {
        Map<String, Double> publishedByTransport = publishedEventCounters.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().count()));

        return Map.of(
                "auth", Map.of(
                        "tokensIssued", authTokenIssuedCounter.count(),
                        "refreshSuccess", authRefreshSuccessCounter.count(),
                        "logoutSuccess", authLogoutSuccessCounter.count(),
                        "logoutAllSuccess", authLogoutAllSuccessCounter.count()
                ),
                "registration", Map.of(
                        "success", registrationSuccessCounter.count(),
                        "rateLimited", registrationRateLimitedCounter.count(),
                        "failure", registrationFailureCounter.count(),
                        "averageDurationMs", registrationTimer.count() == 0
                                ? 0.0
                                : registrationTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS)
                ),
                "outbox", Map.of(
                        "published", outboxPublishedCounter.count(),
                        "retry", outboxRetryCounter.count(),
                        "deadLetter", outboxDeadLetterCounter.count()
                ),
                "messaging", Map.of(
                        "publishedByTransport", publishedByTransport
                )
        );
    }
}
