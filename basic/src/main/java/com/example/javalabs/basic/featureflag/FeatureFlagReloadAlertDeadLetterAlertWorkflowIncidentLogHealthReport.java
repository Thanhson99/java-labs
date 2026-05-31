package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Health report for the bounded incident log that tracks alert workflow failures.
 *
 * <p>Reports are immutable value objects. They are safe to hand to alert policies, dashboards, or
 * tests without exposing mutable monitor state.</p>
 *
 * @param status overall log health
 * @param incidentCount current number of retained incidents
 * @param capacity maximum incidents the log can retain
 * @param utilization incidentCount divided by capacity
 * @param undeliveredCount retained incidents that were not delivered
 * @param droppedCount incidents dropped because the log was full
 * @param warnings operator-facing explanations for non-healthy status
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
        FeatureFlagReloadHealthStatus status,
        int incidentCount,
        int capacity,
        double utilization,
        int undeliveredCount,
        int droppedCount,
        List<String> warnings) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when counters, rates, status, or warnings are invalid
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (incidentCount < 0) {
            throw new IllegalArgumentException("incidentCount must not be negative");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (incidentCount > capacity) {
            throw new IllegalArgumentException("incidentCount must not exceed capacity");
        }
        if (utilization < 0.0 || utilization > 1.0) {
            throw new IllegalArgumentException("utilization must be between 0 and 1");
        }
        if (undeliveredCount < 0) {
            throw new IllegalArgumentException("undeliveredCount must not be negative");
        }
        if (undeliveredCount > incidentCount) {
            throw new IllegalArgumentException("undeliveredCount must not exceed incidentCount");
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
     * @return {@code true} when the incident log does not need operator attention
     */
    public boolean healthy() {
        return status == FeatureFlagReloadHealthStatus.HEALTHY;
    }
}
