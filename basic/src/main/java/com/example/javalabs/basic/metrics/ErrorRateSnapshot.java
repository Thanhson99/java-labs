package com.example.javalabs.basic.metrics;

/**
 * Immutable summary of retained outcomes inside {@link RollingErrorRateWindow}.
 *
 * @param sampleCount number of retained outcomes
 * @param failureCount number of retained failed outcomes
 * @param errorRate failure ratio between {@code 0.0} and {@code 1.0}
 * @param healthy whether the error rate is at or below the configured threshold
 */
public record ErrorRateSnapshot(
        int sampleCount,
        int failureCount,
        double errorRate,
        boolean healthy) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when counts or rate are inconsistent
     */
    public ErrorRateSnapshot {
        if (sampleCount < 0) {
            throw new IllegalArgumentException("sampleCount must not be negative");
        }
        if (failureCount < 0) {
            throw new IllegalArgumentException("failureCount must not be negative");
        }
        if (failureCount > sampleCount) {
            throw new IllegalArgumentException("failureCount must not exceed sampleCount");
        }
        if (errorRate < 0.0 || errorRate > 1.0) {
            throw new IllegalArgumentException("errorRate must be between 0.0 and 1.0");
        }
        if (sampleCount == 0 && errorRate != 0.0) {
            throw new IllegalArgumentException("empty snapshot must have zero error rate");
        }
    }
}
