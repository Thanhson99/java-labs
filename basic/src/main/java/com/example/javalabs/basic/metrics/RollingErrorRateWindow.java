package com.example.javalabs.basic.metrics;

import com.example.javalabs.basic.ServiceCallOutcome;

import java.util.Arrays;

/**
 * Tracks error rate over the most recent fixed number of service-call outcomes.
 *
 * <p>This window is sample-count based instead of time based. It uses a boolean ring buffer and a
 * running failure count so every record and snapshot operation stays constant time regardless of
 * how long the application has been running.</p>
 */
public final class RollingErrorRateWindow {

    private final boolean[] failures;
    private final double unhealthyThreshold;
    private int nextIndex;
    private int sampleCount;
    private int failureCount;

    /**
     * Creates a fixed-size rolling error-rate window.
     *
     * @param capacity maximum number of recent outcomes retained
     * @param unhealthyThreshold error rate above this value is unhealthy
     * @throws IllegalArgumentException when capacity or threshold is invalid
     */
    public RollingErrorRateWindow(int capacity, double unhealthyThreshold) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (unhealthyThreshold < 0.0 || unhealthyThreshold > 1.0) {
            throw new IllegalArgumentException("unhealthyThreshold must be between 0.0 and 1.0");
        }
        this.failures = new boolean[capacity];
        this.unhealthyThreshold = unhealthyThreshold;
    }

    /**
     * Records one service-call outcome and evicts the oldest outcome when full.
     *
     * @param outcome service-call outcome to record
     * @return current error-rate snapshot after recording
     * @throws IllegalArgumentException when {@code outcome} is {@code null}
     */
    public ErrorRateSnapshot record(ServiceCallOutcome outcome) {
        if (outcome == null) {
            throw new IllegalArgumentException("outcome must not be null");
        }

        boolean failed = outcome == ServiceCallOutcome.FAILURE;
        if (sampleCount == failures.length) {
            if (failures[nextIndex]) {
                failureCount--;
            }
        } else {
            sampleCount++;
        }

        failures[nextIndex] = failed;
        if (failed) {
            failureCount++;
        }
        nextIndex = (nextIndex + 1) % failures.length;
        return snapshot();
    }

    /**
     * Returns the current rolling error-rate snapshot.
     *
     * @return immutable snapshot calculated from running counters
     */
    public ErrorRateSnapshot snapshot() {
        double errorRate = sampleCount == 0 ? 0.0 : (double) failureCount / sampleCount;
        return new ErrorRateSnapshot(sampleCount, failureCount, errorRate, errorRate <= unhealthyThreshold);
    }

    /**
     * Clears retained outcomes and resets counters.
     */
    public void clear() {
        Arrays.fill(failures, false);
        nextIndex = 0;
        sampleCount = 0;
        failureCount = 0;
    }

    /**
     * Returns the maximum number of outcomes retained.
     *
     * @return configured capacity
     */
    public int capacity() {
        return failures.length;
    }

    /**
     * Returns the configured unhealthy threshold.
     *
     * @return threshold above which snapshots become unhealthy
     */
    public double unhealthyThreshold() {
        return unhealthyThreshold;
    }
}
