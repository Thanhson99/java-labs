package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatterTest {

    @Test
    void formatsEmptyPlanAsNoActionsMessage() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan(List.of());

        String formatted = new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter().format(plan);

        assertEquals("No incident triage actions.", formatted);
    }

    @Test
    void formatsActionsInPriorityOrder() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan(List.of(
                        new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                                1,
                                FeatureFlagReloadHealthStatus.CRITICAL,
                                "Protect incident history",
                                "Incident log dropped 4 entries; increase capacity."
                        ),
                        new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                                2,
                                FeatureFlagReloadHealthStatus.WARNING,
                                "Review critical workflow incidents",
                                "Review recent workflow health."
                        )
                ));

        String formatted = new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter().format(plan);

        assertEquals("""
                Incident triage plan (highest severity: CRITICAL)
                1. [CRITICAL] Protect incident history - Incident log dropped 4 entries; increase capacity.
                2. [WARNING] Review critical workflow incidents - Review recent workflow health.""", formatted);
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter formatter =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter();

        assertThrows(IllegalArgumentException.class, () -> formatter.format(null));
    }
}
