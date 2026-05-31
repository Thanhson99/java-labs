package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlannerTest {

    @Test
    void returnsEmptyPlanForCleanSummary() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner().plan(summary);

        assertFalse(plan.hasActions());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, plan.highestSeverity());
        assertTrue(plan.actions().isEmpty());
    }

    @Test
    void prioritizesDroppedHistoryBeforeDeliveryProblems() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        3, 2, 1, 1, 2, 1, 1, 1, 4, 1.0 / 3.0);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner().plan(summary);

        assertTrue(plan.hasActions());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, plan.highestSeverity());
        assertEquals(3, plan.actions().size());
        assertEquals(List.of(
                "Protect incident history",
                "Investigate alert delivery",
                "Review critical workflow incidents"
        ), plan.actions().stream()
                .map(FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction::title)
                .toList());
        assertEquals(List.of(1, 2, 3), plan.actions().stream()
                .map(FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction::priority)
                .toList());
    }

    @Test
    void exposesImmutableActions() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        1, 1, 0, 1, 0, 1, 0, 0, 0, 1.0);
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner().plan(summary);

        assertThrows(UnsupportedOperationException.class,
                () -> plan.actions().add(plan.actions().get(0)));
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner planner =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner();

        assertThrows(IllegalArgumentException.class, () -> planner.plan(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                        0,
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        "title",
                        "detail"
                ));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                        1,
                        FeatureFlagReloadHealthStatus.HEALTHY,
                        "title",
                        "detail"
                ));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                        1,
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        "",
                        "detail"
                ));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageAction(
                        1,
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        "title",
                        ""
                ));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan(null));
    }
}
