package com.example.javalabs.basic.metrics;

/**
 * Immutable snapshot of latency values retained by {@link RollingLatencyWindow}.
 *
 * @param sampleCount number of retained samples
 * @param averageMillis average latency across retained samples
 * @param minMillis smallest retained latency
 * @param maxMillis largest retained latency
 */
public record LatencyWindowSnapshot(
        int sampleCount,
        double averageMillis,
        long minMillis,
        long maxMillis) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when counts or latency values are inconsistent
     */
    public LatencyWindowSnapshot {
        if (sampleCount < 0) {
            throw new IllegalArgumentException("sampleCount must not be negative");
        }
        if (averageMillis < 0) {
            throw new IllegalArgumentException("averageMillis must not be negative");
        }
        if (minMillis < 0) {
            throw new IllegalArgumentException("minMillis must not be negative");
        }
        if (maxMillis < 0) {
            throw new IllegalArgumentException("maxMillis must not be negative");
        }
        if (sampleCount == 0 && (averageMillis != 0 || minMillis != 0 || maxMillis != 0)) {
            throw new IllegalArgumentException("empty snapshot must have zero latency values");
        }
        if (sampleCount > 0 && minMillis > maxMillis) {
            throw new IllegalArgumentException("minMillis must not exceed maxMillis");
        }
        if (sampleCount > 0 && (averageMillis < minMillis || averageMillis > maxMillis)) {
            throw new IllegalArgumentException("averageMillis must stay between minMillis and maxMillis");
        }
    }

    /**
     * Creates an empty snapshot.
     *
     * @return snapshot with no retained latency samples
     */
    public static LatencyWindowSnapshot empty() {
        return new LatencyWindowSnapshot(0, 0.0, 0, 0);
    }
}
