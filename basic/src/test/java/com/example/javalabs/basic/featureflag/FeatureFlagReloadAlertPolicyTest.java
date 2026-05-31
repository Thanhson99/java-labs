package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertPolicyTest {

    @Test
    void doesNotAlertForHealthyReport() {
        FeatureFlagReloadAlertPolicy policy = new FeatureFlagReloadAlertPolicy(true);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadHealthReport(
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
    void canSuppressWarningAlerts() {
        FeatureFlagReloadAlertPolicy policy = new FeatureFlagReloadAlertPolicy(false);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadHealthReport(
                FeatureFlagReloadHealthStatus.WARNING,
                List.of("reload block rate is elevated: 30%"),
                0.30,
                0.0,
                0.0
        ));

        assertFalse(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, alert.severity());
    }

    @Test
    void alertsForWarningWhenEnabled() {
        FeatureFlagReloadAlertPolicy policy = new FeatureFlagReloadAlertPolicy(true);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadHealthReport(
                FeatureFlagReloadHealthStatus.WARNING,
                List.of("config submissions are waiting without flushed attempts"),
                0.0,
                0.0,
                0.0
        ));

        assertTrue(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.WARNING, alert.severity());
        assertEquals(List.of("config submissions are waiting without flushed attempts"), alert.details());
    }

    @Test
    void alwaysAlertsForCriticalReport() {
        FeatureFlagReloadAlertPolicy policy = new FeatureFlagReloadAlertPolicy(false);

        FeatureFlagReloadAlert alert = policy.evaluate(new FeatureFlagReloadHealthReport(
                FeatureFlagReloadHealthStatus.CRITICAL,
                List.of("reload rejection rate is critical: 60%"),
                0.0,
                0.60,
                0.0
        ));

        assertTrue(alert.active());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, alert.severity());
        assertEquals("feature flag reload workflow needs attention", alert.message());
        assertEquals(List.of("reload rejection rate is critical: 60%"), alert.details());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertPolicy policy = new FeatureFlagReloadAlertPolicy(true);

        assertThrows(IllegalArgumentException.class, () -> policy.evaluate(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlert(false, null, "ok", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlert(false, FeatureFlagReloadHealthStatus.HEALTHY, "", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlert(false, FeatureFlagReloadHealthStatus.HEALTHY, "ok", null));
        assertThrows(IllegalArgumentException.class,
                () -> FeatureFlagReloadAlert.active(FeatureFlagReloadHealthStatus.HEALTHY, List.of()));
    }
}
