package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds prioritized operator actions from incident-log summary counters.
 *
 * <p>The planner is intentionally rule-based for readability. Each rule maps one operational smell
 * to one action, making it easy for learners to trace why an action appears in the output.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner {

    /**
     * Creates a prioritized plan from a summary.
     *
     * @param summary incident-log summary produced by the summarizer
     * @return immutable action plan ordered by priority
     * @throws IllegalArgumentException when {@code summary} is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan(
            FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary) {
        if (summary == null) {
            throw new IllegalArgumentException("summary must not be null");
        }

        List<FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction> actions = new ArrayList<>();
        if (summary.droppedIncidents() > 0) {
            actions.add(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                    1,
                    FeatureFlagReloadHealthStatus.CRITICAL,
                    "Protect incident history",
                    "Incident log dropped " + summary.droppedIncidents()
                            + " entries; increase capacity or export incidents to durable storage."
            ));
        }
        if (summary.undeliveredIncidents() > 0) {
            actions.add(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                    2,
                    FeatureFlagReloadHealthStatus.CRITICAL,
                    "Investigate alert delivery",
                    summary.undeliveredIncidents()
                            + " incident alerts were not delivered; inspect suppression and sink delivery."
            ));
        }
        if (summary.criticalIncidents() > 0) {
            actions.add(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                    3,
                    FeatureFlagReloadHealthStatus.WARNING,
                    "Review critical workflow incidents",
                    summary.criticalIncidents()
                            + " retained incidents were critical; review workflow health and recent alerts."
            ));
        }

        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan(actions);
    }
}
