package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts dead-letter alert workflow health into an alert payload.
 *
 * <p>This policy alerts on the health of the alert workflow itself, not on the original feature flag
 * reload flow. Keeping that distinction in the message and details makes operator triage clearer.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy {

    private static final String ACTIVE_MESSAGE =
            "feature flag reload dead-letter alert workflow needs attention";

    private final boolean alertOnWarning;

    /**
     * Creates a policy for workflow-health alerts.
     *
     * @param alertOnWarning whether warning-level workflow health should emit an active alert
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(boolean alertOnWarning) {
        this.alertOnWarning = alertOnWarning;
    }

    /**
     * Converts workflow health into an active or inactive alert.
     *
     * @param report workflow health report
     * @return inactive alert for healthy or suppressed-warning reports; otherwise an active alert
     * @throws IllegalArgumentException when {@code report} is {@code null}
     */
    public FeatureFlagReloadAlert evaluate(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report) {
        if (report == null) {
            throw new IllegalArgumentException("report must not be null");
        }
        if (report.healthy()) {
            return FeatureFlagReloadAlert.inactive();
        }
        if (report.status() == FeatureFlagReloadHealthStatus.WARNING && !alertOnWarning) {
            return FeatureFlagReloadAlert.inactive();
        }
        return new FeatureFlagReloadAlert(
                true,
                report.status(),
                ACTIVE_MESSAGE,
                detailsFrom(report)
        );
    }

    /**
     * Builds alert details from health warnings and derived workflow rates.
     *
     * @param report workflow health report
     * @return details suitable for operator alert payloads
     */
    private static List<String> detailsFrom(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report) {
        List<String> details = new ArrayList<>(report.warnings());
        details.add("workflow critical rate: " + percentage(report.criticalRate()));
        details.add("workflow suppression rate: " + percentage(report.suppressionRate()));
        details.add("workflow delivery rate: " + percentage(report.deliveryRate()));
        return details;
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
