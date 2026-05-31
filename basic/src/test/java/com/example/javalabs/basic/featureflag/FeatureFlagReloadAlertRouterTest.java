package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertRouterTest {

    @Test
    void routesSuppressedAlertsToNone() {
        FeatureFlagReloadAlertRouter router = new FeatureFlagReloadAlertRouter();
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                FeatureFlagReloadAlert.inactive(),
                false,
                "inactive alert",
                100
        );

        FeatureFlagReloadAlertRoute route = router.route(decision);

        assertEquals(FeatureFlagReloadAlertChannel.NONE, route.channel());
        assertFalse(route.deliverable());
        assertEquals("alert suppressed: inactive alert", route.summary());
    }

    @Test
    void routesWarningAlertsToDashboard() {
        FeatureFlagReloadAlertRouter router = new FeatureFlagReloadAlertRouter();
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                FeatureFlagReloadAlert.active(
                        FeatureFlagReloadHealthStatus.WARNING,
                        List.of("reload block rate is elevated: 30%")
                ),
                true,
                "alert emitted",
                1_000
        );

        FeatureFlagReloadAlertRoute route = router.route(decision);

        assertEquals(FeatureFlagReloadAlertChannel.DASHBOARD, route.channel());
        assertTrue(route.deliverable());
        assertEquals("warning reload alert routed to dashboard", route.summary());
    }

    @Test
    void routesCriticalAlertsToOnCall() {
        FeatureFlagReloadAlertRouter router = new FeatureFlagReloadAlertRouter();
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                FeatureFlagReloadAlert.active(
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        List.of("reload rejection rate is critical: 60%")
                ),
                true,
                "alert emitted",
                1_000
        );

        FeatureFlagReloadAlertRoute route = router.route(decision);

        assertEquals(FeatureFlagReloadAlertChannel.ON_CALL, route.channel());
        assertTrue(route.deliverable());
        assertEquals("critical reload alert routed to on-call", route.summary());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertRouter router = new FeatureFlagReloadAlertRouter();
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                FeatureFlagReloadAlert.inactive(),
                false,
                "inactive alert",
                0
        );

        assertThrows(IllegalArgumentException.class, () -> router.route(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertRoute(null, decision, "summary"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertRoute(FeatureFlagReloadAlertChannel.NONE, null, "summary"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertRoute(FeatureFlagReloadAlertChannel.NONE, decision, ""));
    }
}
