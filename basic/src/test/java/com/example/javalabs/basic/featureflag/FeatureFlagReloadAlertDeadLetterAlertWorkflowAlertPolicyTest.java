package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicyTest {

    @Test
    void doesNotAlertForHealthyWorkflowHealth() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(true);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                FeatureFlagReloadHealthStatus.HEALTHY,
                List.of(),
                0.0,
                0.0,
                0.0
        ));

        assertFalse(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, alert.severity());
        assertEquals(List.of(), alert.details());
    }

    @Test
    void canSuppressWarningWorkflowAlerts() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(false);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                FeatureFlagReloadHealthStatus.WARNING,
                List.of("dead-letter alert workflow critical rate is elevated: 30%"),
                0.3,
                0.0,
                1.0
        ));

        assertFalse(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, alert.severity());
    }

    @Test
    void alertsForWarningWhenEnabled() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(true);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                FeatureFlagReloadHealthStatus.WARNING,
                List.of("dead-letter alert workflow critical rate is elevated: 30%"),
                0.3,
                0.0,
                1.0
        ));

        assertTrue(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.WARNING, alert.severity());
        assertEquals("feature flag reload dead-letter alert workflow needs attention", alert.message());
        assertEquals(List.of(
                "dead-letter alert workflow critical rate is elevated: 30%",
                "workflow critical rate: 30%",
                "workflow suppression rate: 0%",
                "workflow delivery rate: 100%"
        ), alert.details());
    }

    @Test
    void alwaysAlertsForCriticalWorkflowHealth() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(false);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                FeatureFlagReloadHealthStatus.CRITICAL,
                List.of("critical dead-letter alerts were observed but none were delivered"),
                1.0,
                1.0,
                0.0
        ));

        assertTrue(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, alert.severity());
        assertEquals(List.of(
                "critical dead-letter alerts were observed but none were delivered",
                "workflow critical rate: 100%",
                "workflow suppression rate: 100%",
                "workflow delivery rate: 0%"
        ), alert.details());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(true);

        assertThrows(IllegalArgumentException.class, () -> policy.evaluate(null));
    }
}
