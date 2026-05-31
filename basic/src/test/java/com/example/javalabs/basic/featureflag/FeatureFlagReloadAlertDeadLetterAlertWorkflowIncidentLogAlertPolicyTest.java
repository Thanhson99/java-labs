package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicyTest {

    @Test
    void doesNotAlertForHealthyIncidentLog() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(true);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                FeatureFlagReloadHealthStatus.HEALTHY,
                1,
                10,
                0.1,
                0,
                0,
                List.of()
        ));

        assertFalse(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, alert.severity());
        assertEquals(List.of(), alert.details());
    }

    @Test
    void canSuppressWarningIncidentLogAlerts() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(false);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                FeatureFlagReloadHealthStatus.WARNING,
                2,
                4,
                0.5,
                0,
                0,
                List.of("incident log utilization is elevated: 50%")
        ));

        assertFalse(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, alert.severity());
    }

    @Test
    void alertsForWarningWhenEnabled() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(true);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                FeatureFlagReloadHealthStatus.WARNING,
                2,
                4,
                0.5,
                0,
                0,
                List.of("incident log utilization is elevated: 50%")
        ));

        assertTrue(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.WARNING, alert.severity());
        assertEquals("feature flag reload dead-letter alert workflow incident log needs attention",
                alert.message());
        assertEquals(List.of(
                "incident log utilization is elevated: 50%",
                "incident log utilization: 50%",
                "incident log retained: 2/4",
                "incident log undelivered: 0",
                "incident log dropped: 0"
        ), alert.details());
    }

    @Test
    void alwaysAlertsForCriticalIncidentLogHealth() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(false);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogHealthReport(
                FeatureFlagReloadHealthStatus.CRITICAL,
                2,
                2,
                1.0,
                2,
                1,
                List.of("incident log dropped 1 incidents")
        ));

        assertTrue(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, alert.severity());
        assertEquals(List.of(
                "incident log dropped 1 incidents",
                "incident log utilization: 100%",
                "incident log retained: 2/2",
                "incident log undelivered: 2",
                "incident log dropped: 1"
        ), alert.details());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(true);

        assertThrows(IllegalArgumentException.class, () -> policy.evaluate(null));
    }
}
