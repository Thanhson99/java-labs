package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts dead-letter backlog pressure into an operator-facing health report.
 *
 * <p>The monitor observes store pressure and dropped records. It does not emit alerts directly;
 * that keeps health analysis separate from alert policy and delivery concerns.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterMonitor {

    private final double warningUtilization;
    private final double criticalUtilization;
    private final int maxDroppedCount;

    /**
     * Creates a monitor with backlog utilization and dropped-record thresholds.
     *
     * @param warningUtilization utilization ratio that starts warning status
     * @param criticalUtilization utilization ratio that starts critical status
     * @param maxDroppedCount allowed dropped-record count before critical status
     * @throws IllegalArgumentException when thresholds are outside {@code [0, 1]}, ordered incorrectly,
     *                                  or count thresholds are negative
     */
    public FeatureFlagReloadAlertDeadLetterMonitor(
            double warningUtilization,
            double criticalUtilization,
            int maxDroppedCount) {
        if (warningUtilization < 0 || warningUtilization > 1) {
            throw new IllegalArgumentException("warningUtilization must be between 0 and 1");
        }
        if (criticalUtilization < 0 || criticalUtilization > 1) {
            throw new IllegalArgumentException("criticalUtilization must be between 0 and 1");
        }
        if (warningUtilization > criticalUtilization) {
            throw new IllegalArgumentException("warningUtilization must not exceed criticalUtilization");
        }
        if (maxDroppedCount < 0) {
            throw new IllegalArgumentException("maxDroppedCount must not be negative");
        }
        this.warningUtilization = warningUtilization;
        this.criticalUtilization = criticalUtilization;
        this.maxDroppedCount = maxDroppedCount;
    }

    /**
     * Analyzes the current dead-letter store state.
     *
     * @param store dead-letter store to inspect
     * @return immutable health report with utilization, dropped count, and warnings
     * @throws IllegalArgumentException when {@code store} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterHealthReport analyze(
            FeatureFlagReloadAlertDeadLetterStore store) {
        if (store == null) {
            throw new IllegalArgumentException("store must not be null");
        }

        int backlogSize = store.size();
        int capacity = store.capacity();
        int droppedCount = store.droppedCount();
        double utilization = (double) backlogSize / capacity;
        List<String> warnings = new ArrayList<>();

        // Empty stores should stay healthy even if callers configure a zero warning threshold.
        if (utilization >= warningUtilization && backlogSize > 0) {
            warnings.add("dead-letter backlog utilization is " + percentage(utilization));
        }
        if (droppedCount > maxDroppedCount) {
            warnings.add("dead-letter store dropped " + droppedCount + " records");
        }

        FeatureFlagReloadHealthStatus status = FeatureFlagReloadHealthStatus.HEALTHY;
        // Dropped records mean payload loss, so they can escalate health independent of utilization.
        if ((utilization >= criticalUtilization && backlogSize > 0) || droppedCount > maxDroppedCount) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
        } else if (!warnings.isEmpty()) {
            status = FeatureFlagReloadHealthStatus.WARNING;
        }

        return new FeatureFlagReloadAlertDeadLetterHealthReport(
                status,
                backlogSize,
                capacity,
                utilization,
                droppedCount,
                warnings
        );
    }

    /**
     * Formats a ratio as a whole percentage.
     *
     * @param value ratio between 0 and 1
     * @return percentage text
     */
    private static String percentage(double value) {
        return Math.round(value * 100) + "%";
    }
}
