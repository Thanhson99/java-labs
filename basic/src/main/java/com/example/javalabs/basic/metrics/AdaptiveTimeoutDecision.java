package com.example.javalabs.basic.metrics;

/**
 * Timeout decision derived from latency percentile data.
 *
 * @param timeoutMillis selected timeout after applying margin and clamps
 * @param baselineLatencyMillis percentile latency used as the baseline
 * @param percentile percentile used to calculate the baseline
 * @param sampleCount number of latency samples available in the histogram
 */
public record AdaptiveTimeoutDecision(
        long timeoutMillis,
        long baselineLatencyMillis,
        double percentile,
        long sampleCount) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when timeout, baseline, percentile, or sample count is invalid
     */
    public AdaptiveTimeoutDecision {
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("timeoutMillis must be positive");
        }
        if (baselineLatencyMillis < 0) {
            throw new IllegalArgumentException("baselineLatencyMillis must not be negative");
        }
        if (percentile < 0.0 || percentile > 1.0) {
            throw new IllegalArgumentException("percentile must be between 0.0 and 1.0");
        }
        if (sampleCount < 0) {
            throw new IllegalArgumentException("sampleCount must not be negative");
        }
    }
}
