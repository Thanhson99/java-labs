package com.example.javalabs.basic.featureflag;

/**
 * One recommended operator action derived from incident-log summary counters.
 *
 * <p>The action is intentionally small and immutable so a triage plan can be rendered in a console,
 * dashboard, or test without exposing mutable planner state.</p>
 *
 * @param priority lower numbers should be handled first
 * @param severity action severity
 * @param title concise action title
 * @param detail explanation that gives the operator enough context to start
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
        int priority,
        FeatureFlagReloadHealthStatus severity,
        String title,
        String detail) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when priority, severity, title, or detail is invalid
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction {
        if (priority <= 0) {
            throw new IllegalArgumentException("priority must be positive");
        }
        if (severity == null) {
            throw new IllegalArgumentException("severity must not be null");
        }
        if (severity == FeatureFlagReloadHealthStatus.HEALTHY) {
            throw new IllegalArgumentException("severity must not be HEALTHY");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (detail == null || detail.isBlank()) {
            throw new IllegalArgumentException("detail must not be blank");
        }
    }
}
