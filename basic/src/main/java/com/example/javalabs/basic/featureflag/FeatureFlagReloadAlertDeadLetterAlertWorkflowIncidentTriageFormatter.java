package com.example.javalabs.basic.featureflag;

import java.util.StringJoiner;

/**
 * Formats an incident triage plan into deterministic operator text.
 *
 * <p>The formatter deliberately has no business rules. It only renders an already-built plan, which
 * keeps triage decisions in the planner and presentation concerns in this class.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter {

    private static final String NO_ACTIONS_MESSAGE = "No incident triage actions.";

    /**
     * Formats a triage plan as a newline-delimited checklist.
     *
     * @param plan triage plan to render
     * @return stable text representation suitable for logs, console output, or dashboard previews
     * @throws IllegalArgumentException when {@code plan} is {@code null}
     */
    public String format(FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("plan must not be null");
        }
        if (!plan.hasActions()) {
            return NO_ACTIONS_MESSAGE;
        }

        StringJoiner lines = new StringJoiner("\n");
        lines.add("Incident triage plan (highest severity: " + plan.highestSeverity() + ")");
        for (FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction action : plan.actions()) {
            lines.add(action.priority() + ". [" + action.severity() + "] "
                    + action.title() + " - " + action.detail());
        }
        return lines.toString();
    }
}
