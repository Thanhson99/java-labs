package com.example.javalabs.basic.metrics;

/**
 * Immutable percentile estimate from a latency histogram.
 *
 * @param sampleCount number of samples recorded in the histogram
 * @param percentile requested percentile between {@code 0.0} and {@code 1.0}
 * @param estimatedLatencyMillis bucket upper bound that contains the percentile sample
 */
public record LatencyPercentileSnapshot(
        long sampleCount,
        double percentile,
        long estimatedLatencyMillis) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when sample count, percentile, or latency is invalid
     */
    public LatencyPercentileSnapshot {
        if (sampleCount < 0) {
            throw new IllegalArgumentException("sampleCount must not be negative");
        }
        if (percentile < 0.0 || percentile > 1.0) {
            throw new IllegalArgumentException("percentile must be between 0.0 and 1.0");
        }
        if (estimatedLatencyMillis < 0) {
            throw new IllegalArgumentException("estimatedLatencyMillis must not be negative");
        }
    }
}
