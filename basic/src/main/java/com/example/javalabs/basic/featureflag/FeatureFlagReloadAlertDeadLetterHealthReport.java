package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Operational health report for the reload-alert dead-letter backlog.
 *
 * <p>The report is immutable and validates its own ratios, so alert policies and dashboards can use
 * it without defensive copying or recalculating the monitor's invariants.</p>
 *
 * @param status severity derived from backlog utilization and dropped records
 * @param backlogSize number of records currently stored
 * @param capacity maximum records the store can hold
 * @param utilization backlogSize divided by capacity
 * @param droppedCount number of oldest records dropped because the store was full
 * @param warnings human-readable reasons behind non-healthy status
 */
public record FeatureFlagReloadAlertDeadLetterHealthReport(
        FeatureFlagReloadHealthStatus status,
        int backlogSize,
        int capacity,
        double utilization,
        int droppedCount,
        List<String> warnings) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when counters, ratios, status, or warnings are invalid
     */
    public FeatureFlagReloadAlertDeadLetterHealthReport {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (backlogSize < 0) {
            throw new IllegalArgumentException("backlogSize must not be negative");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (backlogSize > capacity) {
            throw new IllegalArgumentException("backlogSize must not exceed capacity");
        }
        if (utilization < 0 || utilization > 1) {
            throw new IllegalArgumentException("utilization must be between 0 and 1");
        }
        if (droppedCount < 0) {
            throw new IllegalArgumentException("droppedCount must not be negative");
        }
        if (warnings == null) {
            throw new IllegalArgumentException("warnings must not be null");
        }
        warnings = List.copyOf(warnings);
    }

    /**
     * @return {@code true} when the dead-letter backlog does not need operator attention
     */
    public boolean healthy() {
        return status == FeatureFlagReloadHealthStatus.HEALTHY;
    }
}
