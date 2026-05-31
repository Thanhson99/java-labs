package com.example.javalabs.basic.metrics;

/**
 * Decision produced after evaluating one latency sample against recent baseline latency.
 *
 * @param spike whether the new latency sample is considered a spike
 * @param latencyMillis latency sample that was evaluated
 * @param baselineAverageMillis rolling average before the new sample was recorded
 * @param thresholdMillis latency threshold used for the decision
 * @param sampleCountBefore number of retained baseline samples before recording the new value
 * @param snapshotAfter snapshot after the new sample was recorded
 */
public record LatencySpikeDecision(
        boolean spike,
        long latencyMillis,
        double baselineAverageMillis,
        double thresholdMillis,
        int sampleCountBefore,
        LatencyWindowSnapshot snapshotAfter) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when numeric values are negative
     * @throws IllegalArgumentException when {@code snapshotAfter} is {@code null}
     */
    public LatencySpikeDecision {
        if (latencyMillis < 0) {
            throw new IllegalArgumentException("latencyMillis must not be negative");
        }
        if (baselineAverageMillis < 0) {
            throw new IllegalArgumentException("baselineAverageMillis must not be negative");
        }
        if (thresholdMillis < 0) {
            throw new IllegalArgumentException("thresholdMillis must not be negative");
        }
        if (sampleCountBefore < 0) {
            throw new IllegalArgumentException("sampleCountBefore must not be negative");
        }
        if (snapshotAfter == null) {
            throw new IllegalArgumentException("snapshotAfter must not be null");
        }
    }
}
