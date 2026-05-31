package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Summary produced from feature flag reload metrics.
 *
 * @param status overall health level
 * @param warnings human-readable warnings for operators
 * @param blockRate rate-limit block ratio among flushed attempts
 * @param rejectionRate safe reload rejection ratio among changed configs
 * @param skipRate fingerprint skip ratio among flushed attempts
 */
public record FeatureFlagReloadHealthReport(
        FeatureFlagReloadHealthStatus status,
        List<String> warnings,
        double blockRate,
        double rejectionRate,
        double skipRate) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when status, warnings, or rates are invalid
     */
    public FeatureFlagReloadHealthReport {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (warnings == null) {
            throw new IllegalArgumentException("warnings must not be null");
        }
        warnings = List.copyOf(warnings);
        validateRate(blockRate, "blockRate");
        validateRate(rejectionRate, "rejectionRate");
        validateRate(skipRate, "skipRate");
    }

    /**
     * @return {@code true} when the report has healthy status
     */
    public boolean healthy() {
        return status == FeatureFlagReloadHealthStatus.HEALTHY;
    }

    /**
     * @return {@code true} when operators should inspect the workflow
     */
    public boolean needsAttention() {
        return status != FeatureFlagReloadHealthStatus.HEALTHY;
    }

    /**
     * Validates a rate ratio.
     *
     * @throws IllegalArgumentException when {@code value} is outside 0..1
     */
    private static void validateRate(double value, String name) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
