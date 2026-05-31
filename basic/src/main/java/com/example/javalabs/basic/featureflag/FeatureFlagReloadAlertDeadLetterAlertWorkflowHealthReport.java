package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Health summary derived from dead-letter alert workflow metrics.
 *
 * <p>This report is the bridge between raw counters and alert policy. It stores only derived rates
 * and human-readable warnings so downstream code does not need to recalculate metric formulas.</p>
 *
 * @param status overall workflow health
 * @param warnings operator-facing explanations for non-healthy status
 * @param criticalRate critical health reports divided by workflow runs
 * @param suppressionRate suppressed alerts divided by active alerts
 * @param deliveryRate delivered dispatches divided by active alerts
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
        FeatureFlagReloadHealthStatus status,
        List<String> warnings,
        double criticalRate,
        double suppressionRate,
        double deliveryRate) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when status, warnings, or rates are invalid
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (warnings == null) {
            throw new IllegalArgumentException("warnings must not be null");
        }
        warnings = List.copyOf(warnings);
        validateRate(criticalRate, "criticalRate");
        validateRate(suppressionRate, "suppressionRate");
        validateRate(deliveryRate, "deliveryRate");
    }

    /**
     * @return {@code true} when the workflow metrics do not require attention
     */
    public boolean healthy() {
        return status == FeatureFlagReloadHealthStatus.HEALTHY;
    }

    /**
     * Validates a rate ratio.
     *
     * @param value rate value
     * @param name parameter name for error messages
     * @throws IllegalArgumentException when {@code value} is outside 0..1
     */
    private static void validateRate(double value, String name) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
