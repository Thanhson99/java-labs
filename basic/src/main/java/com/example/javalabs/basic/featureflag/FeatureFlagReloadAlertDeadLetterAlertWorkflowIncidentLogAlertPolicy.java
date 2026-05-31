package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts incident-log health into an alert payload that can reuse the shared alert pipeline.
 *
 * <p>The policy owns the alerting decision only. It does not suppress duplicates, choose a channel,
 * or deliver the alert; those responsibilities stay in the suppressor, router, and dispatcher.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy {

    private static final String ACTIVE_MESSAGE =
            "feature flag reload dead-letter alert workflow incident log needs attention";

    private final boolean alertOnWarning;

    /**
     * Creates a policy for incident-log health alerts.
     *
     * @param alertOnWarning whether {@link FeatureFlagReloadHealthStatus#WARNING} reports should emit alerts
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(boolean alertOnWarning) {
        this.alertOnWarning = alertOnWarning;
    }

    /**
     * Converts a health report into an active or inactive alert.
     *
     * @param report incident-log health report produced by the monitor
     * @return inactive alert for healthy or suppressed-warning reports; otherwise an active alert
     * @throws IllegalArgumentException when {@code report} is {@code null}
     */
    public FeatureFlagReloadAlert evaluate(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport report) {
        if (report == null) {
            throw new IllegalArgumentException("report must not be null");
        }
        if (report.healthy()) {
            return FeatureFlagReloadAlert.inactive();
        }
        if (report.status() == FeatureFlagReloadHealthStatus.WARNING && !alertOnWarning) {
            return FeatureFlagReloadAlert.inactive();
        }
        // Add summary counters to the monitor warnings so the routed alert is self-contained.
        return new FeatureFlagReloadAlert(
                true,
                report.status(),
                ACTIVE_MESSAGE,
                detailsFrom(report)
        );
    }

    /**
     * Builds alert details from monitor warnings and incident-log counters.
     *
     * @param report incident-log health report
     * @return operator-facing details
     */
    private static List<String> detailsFrom(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport report) {
        List<String> details = new ArrayList<>(report.warnings());
        details.add("incident log utilization: " + percentage(report.utilization()));
        details.add("incident log retained: " + report.incidentCount() + "/" + report.capacity());
        details.add("incident log undelivered: " + report.undeliveredCount());
        details.add("incident log dropped: " + report.droppedCount());
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
