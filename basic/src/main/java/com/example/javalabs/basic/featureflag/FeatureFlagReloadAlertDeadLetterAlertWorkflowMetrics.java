package com.example.javalabs.basic.featureflag;

/**
 * Mutable counters for dead-letter alert workflow outcomes.
 *
 * <p>The metrics object is intentionally simple and in-memory for practice. Production code would
 * usually export equivalent counters to a metrics backend instead of keeping them only in process.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowMetrics {

    private int runs;
    private int healthyReports;
    private int warningReports;
    private int criticalReports;
    private int activeAlerts;
    private int suppressedAlerts;
    private int deliveredAlerts;
    private int skippedDispatches;

    /**
     * Records one completed workflow result.
     *
     * @param result workflow result to classify into counters
     * @throws IllegalArgumentException when {@code result} is {@code null}
     */
    public void record(FeatureFlagReloadAlertDeadLetterAlertWorkflowResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }

        runs++;
        // Classify the source health status before classifying alert and delivery outcomes.
        if (result.healthReport().status() == FeatureFlagReloadHealthStatus.HEALTHY) {
            healthyReports++;
        } else if (result.healthReport().status() == FeatureFlagReloadHealthStatus.WARNING) {
            warningReports++;
        } else {
            criticalReports++;
        }

        if (result.alert().active()) {
            activeAlerts++;
        }
        if (result.decision().suppressed()) {
            suppressedAlerts++;
        }
        if (result.delivered()) {
            deliveredAlerts++;
        } else {
            skippedDispatches++;
        }
    }

    /**
     * Returns an immutable snapshot of current counters.
     *
     * @return immutable metrics snapshot
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot snapshot() {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                runs,
                healthyReports,
                warningReports,
                criticalReports,
                activeAlerts,
                suppressedAlerts,
                deliveredAlerts,
                skippedDispatches
        );
    }
}
