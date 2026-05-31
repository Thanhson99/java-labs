package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes incident-log pressure and undelivered incident risk.
 *
 * <p>This monitor turns bounded audit history into a compact health report. It intentionally works
 * from the public log API instead of internal fields, so the log can change its storage
 * implementation without changing monitor behavior.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor {

    private final double warningUtilization;
    private final double criticalUtilization;
    private final int maxUndeliveredCount;
    private final int maxDroppedCount;

    /**
     * Creates an incident-log monitor with utilization and loss thresholds.
     *
     * @param warningUtilization utilization ratio that starts warning status
     * @param criticalUtilization utilization ratio that starts critical status
     * @param maxUndeliveredCount allowed undelivered incident count before critical status
     * @param maxDroppedCount allowed dropped incident count before critical status
     * @throws IllegalArgumentException when thresholds are outside {@code [0, 1]}, ordered incorrectly,
     *                                  or count thresholds are negative
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor(
            double warningUtilization,
            double criticalUtilization,
            int maxUndeliveredCount,
            int maxDroppedCount) {
        validateThreshold(warningUtilization, "warningUtilization");
        validateThreshold(criticalUtilization, "criticalUtilization");
        if (warningUtilization > criticalUtilization) {
            throw new IllegalArgumentException("warningUtilization must be <= criticalUtilization");
        }
        if (maxUndeliveredCount < 0) {
            throw new IllegalArgumentException("maxUndeliveredCount must not be negative");
        }
        if (maxDroppedCount < 0) {
            throw new IllegalArgumentException("maxDroppedCount must not be negative");
        }
        this.warningUtilization = warningUtilization;
        this.criticalUtilization = criticalUtilization;
        this.maxUndeliveredCount = maxUndeliveredCount;
        this.maxDroppedCount = maxDroppedCount;
    }

    /**
     * Builds a health report from the current incident-log state.
     *
     * @param log bounded incident log to inspect
     * @return immutable health report with status, ratios, counts, and warnings
     * @throws IllegalArgumentException when {@code log} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport analyze(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log) {
        if (log == null) {
            throw new IllegalArgumentException("log must not be null");
        }

        int incidentCount = log.size();
        int capacity = log.capacity();
        double utilization = (double) incidentCount / capacity;
        int undeliveredCount = (int) log.findAll().stream()
                .filter(incident -> !incident.delivered())
                .count();
        int droppedCount = log.droppedCount();
        List<String> warnings = new ArrayList<>();
        FeatureFlagReloadHealthStatus status = FeatureFlagReloadHealthStatus.HEALTHY;

        // Utilization is meaningful only when the log contains at least one retained incident.
        if (utilization >= criticalUtilization && incidentCount > 0) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
            warnings.add("incident log utilization is " + percentage(utilization));
        } else if (utilization >= warningUtilization && incidentCount > 0) {
            status = FeatureFlagReloadHealthStatus.WARNING;
            warnings.add("incident log utilization is elevated: " + percentage(utilization));
        }

        // Loss or non-delivery means the audit trail is no longer fully reliable.
        if (undeliveredCount > maxUndeliveredCount) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
            warnings.add("incident log has " + undeliveredCount + " undelivered incidents");
        }
        if (droppedCount > maxDroppedCount) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
            warnings.add("incident log dropped " + droppedCount + " incidents");
        }

        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                status,
                incidentCount,
                capacity,
                utilization,
                undeliveredCount,
                droppedCount,
                warnings
        );
    }

    /**
     * Validates a utilization threshold.
     *
     * @param value threshold ratio
     * @param name parameter name for error messages
     * @throws IllegalArgumentException when {@code value} is outside 0..1
     */
    private static void validateThreshold(double value, String name) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }

    /**
     * Formats a ratio as whole percent text.
     *
     * @param value ratio between 0 and 1
     * @return percentage text
     */
    private static String percentage(double value) {
        return Math.round(value * 100) + "%";
    }
}
