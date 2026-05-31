package com.example.javalabs.basic.featureflag;

/**
 * Converts feature flag reload health reports into alert decisions.
 */
public final class FeatureFlagReloadAlertPolicy {

    private final boolean alertOnWarning;

    /**
     * Creates an alert policy.
     *
     * @param alertOnWarning whether warning health should emit active alerts
     */
    public FeatureFlagReloadAlertPolicy(boolean alertOnWarning) {
        this.alertOnWarning = alertOnWarning;
    }

    /**
     * Converts a health report into an active or inactive alert.
     *
     * @param report health report to evaluate
     * @return alert payload decision
     * @throws IllegalArgumentException when {@code report} is {@code null}
     */
    public FeatureFlagReloadAlert evaluate(FeatureFlagReloadHealthReport report) {
        if (report == null) {
            throw new IllegalArgumentException("report must not be null");
        }
        if (report.healthy()) {
            return FeatureFlagReloadAlert.inactive();
        }
        if (report.status() == FeatureFlagReloadHealthStatus.WARNING && !alertOnWarning) {
            // Some teams only page/notify on critical signals and keep warning in dashboards.
            return FeatureFlagReloadAlert.inactive();
        }
        return FeatureFlagReloadAlert.active(report.status(), report.warnings());
    }
}
