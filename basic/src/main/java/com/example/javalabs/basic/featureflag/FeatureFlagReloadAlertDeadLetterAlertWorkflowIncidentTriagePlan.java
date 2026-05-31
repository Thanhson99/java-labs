package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Ordered action plan built from incident-log summary counters.
 *
 * <p>The plan separates summary interpretation from UI rendering. Callers can render the same plan
 * as text, cards, CLI output, or tests without recalculating priorities.</p>
 *
 * @param actions recommended actions ordered by priority
 */
public record FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan(
        List<FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction> actions) {

    /**
     * Validates and defensively copies the generated record constructor argument.
     *
     * @throws IllegalArgumentException when {@code actions} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan {
        if (actions == null) {
            throw new IllegalArgumentException("actions must not be null");
        }
        actions = List.copyOf(actions);
    }

    /**
     * @return {@code true} when the summary produced at least one recommended action
     */
    public boolean hasActions() {
        return !actions.isEmpty();
    }

    /**
     * @return highest severity in the plan, or {@link FeatureFlagReloadHealthStatus#HEALTHY} when empty
     */
    public FeatureFlagReloadHealthStatus highestSeverity() {
        if (actions.stream().anyMatch(action -> action.severity() == FeatureFlagReloadHealthStatus.CRITICAL)) {
            return FeatureFlagReloadHealthStatus.CRITICAL;
        }
        if (actions.stream().anyMatch(action -> action.severity() == FeatureFlagReloadHealthStatus.WARNING)) {
            return FeatureFlagReloadHealthStatus.WARNING;
        }
        return FeatureFlagReloadHealthStatus.HEALTHY;
    }
}
