package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts dead-letter backlog health into an alert payload.
 *
 * <p>The policy enriches health warnings with backlog counters so the emitted alert has enough
 * context even when viewed outside the application logs.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertPolicy {

    private static final String ACTIVE_MESSAGE =
            "feature flag reload alert dead-letter backlog needs attention";

    private final boolean alertOnWarning;

    /**
     * Creates a dead-letter backlog alert policy.
     *
     * @param alertOnWarning whether warning-level backlog health should emit an active alert
     */
    public FeatureFlagReloadAlertDeadLetterAlertPolicy(boolean alertOnWarning) {
        this.alertOnWarning = alertOnWarning;
    }

    /**
     * Converts a dead-letter health report into an alert.
     *
     * @param report dead-letter backlog health report
     * @return inactive alert for healthy or suppressed-warning reports; otherwise an active alert
     * @throws IllegalArgumentException when {@code report} is {@code null}
     */
    public FeatureFlagReloadAlert evaluate(FeatureFlagReloadAlertDeadLetterHealthReport report) {
        if (report == null) {
            throw new IllegalArgumentException("report must not be null");
        }
        if (report.healthy()) {
            return FeatureFlagReloadAlert.inactive();
        }
        if (report.status() == FeatureFlagReloadHealthStatus.WARNING && !alertOnWarning) {
            return FeatureFlagReloadAlert.inactive();
        }
        // Preserve monitor warnings and append the core counters needed for triage.
        return new FeatureFlagReloadAlert(
                true,
                report.status(),
                ACTIVE_MESSAGE,
                detailsFrom(report)
        );
    }

    /**
     * Builds operator-facing alert details from monitor output and backlog counters.
     *
     * @param report dead-letter backlog health report
     * @return alert details with warnings plus key counters
     */
    private static List<String> detailsFrom(FeatureFlagReloadAlertDeadLetterHealthReport report) {
        List<String> details = new ArrayList<>(report.warnings());
        details.add("dead-letter backlog: " + report.backlogSize() + "/" + report.capacity());
        details.add("dead-letter dropped count: " + report.droppedCount());
        return details;
    }
}
