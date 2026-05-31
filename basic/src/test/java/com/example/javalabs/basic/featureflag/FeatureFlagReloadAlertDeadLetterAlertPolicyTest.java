package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertPolicyTest {

    @Test
    void doesNotAlertForHealthyDeadLetterReport() {
        FeatureFlagReloadAlertDeadLetterAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(true);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterHealthReport(
                FeatureFlagReloadHealthStatus.HEALTHY,
                0,
                10,
                0.0,
                0,
                List.of()
        ));

        assertFalse(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, alert.severity());
        assertEquals(List.of(), alert.details());
    }

    @Test
    void canSuppressWarningDeadLetterAlerts() {
        FeatureFlagReloadAlertDeadLetterAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(false);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterHealthReport(
                FeatureFlagReloadHealthStatus.WARNING,
                5,
                10,
                0.5,
                0,
                List.of("dead-letter backlog utilization is 50%")
        ));

        assertFalse(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, alert.severity());
    }

    @Test
    void alertsForWarningWhenEnabled() {
        FeatureFlagReloadAlertDeadLetterAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(true);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterHealthReport(
                FeatureFlagReloadHealthStatus.WARNING,
                5,
                10,
                0.5,
                0,
                List.of("dead-letter backlog utilization is 50%")
        ));

        assertTrue(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.WARNING, alert.severity());
        assertEquals("feature flag reload alert dead-letter backlog needs attention", alert.message());
        assertEquals(List.of(
                "dead-letter backlog utilization is 50%",
                "dead-letter backlog: 5/10",
                "dead-letter dropped count: 0"
        ), alert.details());
    }

    @Test
    void alwaysAlertsForCriticalDeadLetterReport() {
        FeatureFlagReloadAlertDeadLetterAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(false);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterHealthReport(
                FeatureFlagReloadHealthStatus.CRITICAL,
                10,
                10,
                1.0,
                2,
                List.of("dead-letter store dropped 2 records")
        ));

        assertTrue(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, alert.severity());
        assertEquals(List.of(
                "dead-letter store dropped 2 records",
                "dead-letter backlog: 10/10",
                "dead-letter dropped count: 2"
        ), alert.details());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(true);

        assertThrows(IllegalArgumentException.class, () -> policy.evaluate(null));
    }
}
