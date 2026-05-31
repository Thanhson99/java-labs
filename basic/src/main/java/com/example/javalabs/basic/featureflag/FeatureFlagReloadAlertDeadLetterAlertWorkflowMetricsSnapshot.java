package com.example.javalabs.basic.featureflag;

/**
 * Immutable view of dead-letter alert workflow counters.
 *
 * <p>The constructor validates counter relationships so downstream analyzers can trust the snapshot
 * instead of rechecking basic invariants.</p>
 *
 * @param runs total workflow executions observed
 * @param healthyReports number of runs whose health report was healthy
 * @param warningReports number of runs whose health report was warning
 * @param criticalReports number of runs whose health report was critical
 * @param activeAlerts number of runs that produced an active alert
 * @param suppressedAlerts number of runs suppressed by cooldown or inactive alert state
 * @param deliveredAlerts number of runs delivered to the sink
 * @param skippedDispatches number of runs skipped by the dispatcher
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
        int runs,
        int healthyReports,
        int warningReports,
        int criticalReports,
        int activeAlerts,
        int suppressedAlerts,
        int deliveredAlerts,
        int skippedDispatches) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when counters are negative or inconsistent
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot {
        if (runs < 0) {
            throw new IllegalArgumentException("runs must not be negative");
        }
        if (healthyReports < 0) {
            throw new IllegalArgumentException("healthyReports must not be negative");
        }
        if (warningReports < 0) {
            throw new IllegalArgumentException("warningReports must not be negative");
        }
        if (criticalReports < 0) {
            throw new IllegalArgumentException("criticalReports must not be negative");
        }
        if (activeAlerts < 0) {
            throw new IllegalArgumentException("activeAlerts must not be negative");
        }
        if (suppressedAlerts < 0) {
            throw new IllegalArgumentException("suppressedAlerts must not be negative");
        }
        if (deliveredAlerts < 0) {
            throw new IllegalArgumentException("deliveredAlerts must not be negative");
        }
        if (skippedDispatches < 0) {
            throw new IllegalArgumentException("skippedDispatches must not be negative");
        }
        // The health counters and dispatch counters are two complete partitions of all runs.
        if (runs != healthyReports + warningReports + criticalReports) {
            throw new IllegalArgumentException("runs must equal health report counters");
        }
        if (runs != deliveredAlerts + skippedDispatches) {
            throw new IllegalArgumentException("runs must equal dispatch outcome counters");
        }
        if (activeAlerts > runs) {
            throw new IllegalArgumentException("activeAlerts must not exceed runs");
        }
        if (suppressedAlerts > runs) {
            throw new IllegalArgumentException("suppressedAlerts must not exceed runs");
        }
    }
}
