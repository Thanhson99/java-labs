package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts dead-letter alert workflow metrics into a health report.
 *
 * <p>The analyzer works with ratios instead of raw counters so it can compare small and large
 * workloads consistently. It also treats critical reports with zero delivery as critical even when
 * rate thresholds are configured loosely.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer {

    private final double warningCriticalRate;
    private final double criticalCriticalRate;
    private final double warningSuppressionRate;
    private final double criticalSuppressionRate;

    /**
     * Creates a workflow-health analyzer.
     *
     * @param warningCriticalRate critical-report ratio that starts warning status
     * @param criticalCriticalRate critical-report ratio that starts critical status
     * @param warningSuppressionRate suppressed-active-alert ratio that starts warning status
     * @param criticalSuppressionRate suppressed-active-alert ratio that starts critical status
     * @throws IllegalArgumentException when thresholds are outside {@code [0, 1]} or ordered incorrectly
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer(
            double warningCriticalRate,
            double criticalCriticalRate,
            double warningSuppressionRate,
            double criticalSuppressionRate) {
        validateThreshold(warningCriticalRate, "warningCriticalRate");
        validateThreshold(criticalCriticalRate, "criticalCriticalRate");
        validateThreshold(warningSuppressionRate, "warningSuppressionRate");
        validateThreshold(criticalSuppressionRate, "criticalSuppressionRate");
        if (warningCriticalRate > criticalCriticalRate) {
            throw new IllegalArgumentException("warningCriticalRate must be <= criticalCriticalRate");
        }
        if (warningSuppressionRate > criticalSuppressionRate) {
            throw new IllegalArgumentException("warningSuppressionRate must be <= criticalSuppressionRate");
        }
        this.warningCriticalRate = warningCriticalRate;
        this.criticalCriticalRate = criticalCriticalRate;
        this.warningSuppressionRate = warningSuppressionRate;
        this.criticalSuppressionRate = criticalSuppressionRate;
    }

    /**
     * Converts counters into an immutable health report.
     *
     * @param snapshot workflow metrics snapshot
     * @return health report with derived rates and warnings
     * @throws IllegalArgumentException when {@code snapshot} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport analyze(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot must not be null");
        }

        double criticalRate = ratio(snapshot.criticalReports(), snapshot.runs());
        double suppressionRate = ratio(snapshot.suppressedAlerts(), snapshot.activeAlerts());
        double deliveryRate = ratio(snapshot.deliveredAlerts(), snapshot.activeAlerts());
        List<String> warnings = new ArrayList<>();
        FeatureFlagReloadHealthStatus status = FeatureFlagReloadHealthStatus.HEALTHY;

        // Critical backlog alerts appearing frequently means the underlying alert flow needs attention.
        if (criticalRate >= criticalCriticalRate && snapshot.runs() > 0) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
            warnings.add("dead-letter alert workflow critical rate is " + formatRate(criticalRate));
        } else if (criticalRate >= warningCriticalRate && snapshot.runs() > 0) {
            status = max(status, FeatureFlagReloadHealthStatus.WARNING);
            warnings.add("dead-letter alert workflow critical rate is elevated: " + formatRate(criticalRate));
        }

        // Suppression is useful for noise control, but too much suppression can hide repeated failures.
        if (suppressionRate >= criticalSuppressionRate && snapshot.activeAlerts() > 0) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
            warnings.add("dead-letter alert workflow suppression rate is " + formatRate(suppressionRate));
        } else if (suppressionRate >= warningSuppressionRate && snapshot.activeAlerts() > 0) {
            status = max(status, FeatureFlagReloadHealthStatus.WARNING);
            warnings.add("dead-letter alert workflow suppression rate is elevated: " + formatRate(suppressionRate));
        }

        // A critical alert that never reaches a delivery sink should always be visible as critical health.
        if (snapshot.criticalReports() > 0 && snapshot.deliveredAlerts() == 0) {
            status = FeatureFlagReloadHealthStatus.CRITICAL;
            warnings.add("critical dead-letter alerts were observed but none were delivered");
        }

        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                status,
                warnings,
                criticalRate,
                suppressionRate,
                deliveryRate
        );
    }

    private static FeatureFlagReloadHealthStatus max(
            FeatureFlagReloadHealthStatus current,
            FeatureFlagReloadHealthStatus candidate) {
        if (current == FeatureFlagReloadHealthStatus.CRITICAL || candidate == FeatureFlagReloadHealthStatus.CRITICAL) {
            return FeatureFlagReloadHealthStatus.CRITICAL;
        }
        if (current == FeatureFlagReloadHealthStatus.WARNING || candidate == FeatureFlagReloadHealthStatus.WARNING) {
            return FeatureFlagReloadHealthStatus.WARNING;
        }
        return FeatureFlagReloadHealthStatus.HEALTHY;
    }

    /**
     * Calculates a safe ratio when no denominator is available.
     *
     * @param numerator numerator counter
     * @param denominator denominator counter
     * @return ratio, or {@code 0.0} when denominator is zero
     */
    private static double ratio(int numerator, int denominator) {
        if (denominator == 0) {
            return 0.0;
        }
        return (double) numerator / denominator;
    }

    /**
     * Formats a ratio as whole percent text for warnings.
     *
     * @param value ratio between 0 and 1
     * @return percentage text
     */
    private static String formatRate(double value) {
        return Math.round(value * 100.0) + "%";
    }

    /**
     * Validates a threshold ratio.
     *
     * @param value threshold value
     * @param name parameter name for error messages
     * @throws IllegalArgumentException when {@code value} is outside 0..1
     */
    private static void validateThreshold(double value, String name) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
