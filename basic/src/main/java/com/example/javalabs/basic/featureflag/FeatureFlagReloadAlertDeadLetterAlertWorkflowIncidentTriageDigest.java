package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.TimeSource;

/**
 * Export-ready snapshot of incident triage output.
 *
 * <p>The digest groups the derived views that operators usually need together: a numeric summary,
 * a prioritized action plan, a rendered text preview, and the time when the snapshot was generated.
 * It intentionally does not replace the incident log as the source of truth.</p>
 *
 * @param generatedAtMillis timestamp supplied by a {@link TimeSource}
 * @param summary dashboard-friendly summary of retained incidents
 * @param plan prioritized incident triage actions
 * @param formattedPlan stable text representation of the plan
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest(
        long generatedAtMillis,
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary,
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan,
        String formattedPlan) {

    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest {
        if (generatedAtMillis < 0) {
            throw new IllegalArgumentException("generatedAtMillis must not be negative");
        }
        if (summary == null) {
            throw new IllegalArgumentException("summary must not be null");
        }
        if (plan == null) {
            throw new IllegalArgumentException("plan must not be null");
        }
        if (formattedPlan == null || formattedPlan.isBlank()) {
            throw new IllegalArgumentException("formattedPlan must not be blank");
        }
    }

    /**
     * @return {@code true} when the digest contains at least one recommended operator action
     */
    public boolean hasActions() {
        return plan.hasActions();
    }

    /**
     * @return highest severity in the embedded action plan
     */
    public FeatureFlagReloadHealthStatus severity() {
        return plan.highestSeverity();
    }
}


