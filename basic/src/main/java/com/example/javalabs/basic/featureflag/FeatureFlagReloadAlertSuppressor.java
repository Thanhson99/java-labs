package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.TimeSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Suppresses repeated feature flag reload alerts for a cooldown window.
 */
public final class FeatureFlagReloadAlertSuppressor {

    private final long cooldownMillis;
    private final TimeSource timeSource;
    private final Map<String, Long> nextAllowedByFingerprint = new LinkedHashMap<>();

    /**
     * Creates a cooldown suppressor.
     *
     * @param cooldownMillis minimum time between identical alert emissions
     * @param timeSource clock used for deterministic cooldown tests
     * @throws IllegalArgumentException when cooldown or clock is invalid
     */
    public FeatureFlagReloadAlertSuppressor(long cooldownMillis, TimeSource timeSource) {
        if (cooldownMillis <= 0) {
            throw new IllegalArgumentException("cooldownMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.cooldownMillis = cooldownMillis;
        this.timeSource = timeSource;
    }

    /**
     * Applies duplicate suppression to an alert.
     *
     * @param alert alert payload to evaluate
     * @return decision describing whether the alert should be emitted
     * @throws IllegalArgumentException when {@code alert} is {@code null}
     */
    public FeatureFlagReloadAlertDecision evaluate(FeatureFlagReloadAlert alert) {
        if (alert == null) {
            throw new IllegalArgumentException("alert must not be null");
        }
        long now = timeSource.currentTimeMillis();
        if (!alert.active()) {
            return new FeatureFlagReloadAlertDecision(alert, false, "inactive alert", now);
        }

        String fingerprint = fingerprint(alert);
        long nextAllowedAt = nextAllowedByFingerprint.getOrDefault(fingerprint, 0L);
        if (now < nextAllowedAt) {
            // Same alert is still inside cooldown, so suppress this duplicate emission.
            return new FeatureFlagReloadAlertDecision(alert, false, "alert cooldown active", nextAllowedAt);
        }

        long newNextAllowedAt = now + cooldownMillis;
        nextAllowedByFingerprint.put(fingerprint, newNextAllowedAt);
        return new FeatureFlagReloadAlertDecision(alert, true, "alert emitted", newNextAllowedAt);
    }

    /**
     * @return number of distinct alert fingerprints currently tracked
     */
    public int trackedAlertCount() {
        return nextAllowedByFingerprint.size();
    }

    /**
     * Builds a stable key for duplicate-alert suppression.
     *
     * @param alert alert to fingerprint
     * @return stable fingerprint text
     */
    private static String fingerprint(FeatureFlagReloadAlert alert) {
        StringJoiner joiner = new StringJoiner("|");
        joiner.add(alert.severity().name());
        joiner.add(alert.message());
        for (String detail : alert.details()) {
            joiner.add(detail);
        }
        return joiner.toString();
    }
}

