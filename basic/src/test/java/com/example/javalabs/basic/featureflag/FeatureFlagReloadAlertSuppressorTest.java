package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertSuppressorTest {

    @Test
    void suppressesInactiveAlerts() {
        FeatureFlagReloadAlertSuppressor suppressor =
                new FeatureFlagReloadAlertSuppressor(1_000, new ManualTimeSource(100));

        FeatureFlagReloadAlertDecision decision = suppressor.evaluate(FeatureFlagReloadAlert.inactive());

        assertFalse(decision.emitted());
        assertTrue(decision.suppressed());
        assertEquals("inactive alert", decision.reason());
        assertEquals(100, decision.nextAllowedAtMillis());
        assertEquals(0, suppressor.trackedAlertCount());
    }

    @Test
    void emitsFirstActiveAlertAndSuppressesDuplicateDuringCooldown() {
        ManualTimeSource clock = new ManualTimeSource(1_000);
        FeatureFlagReloadAlertSuppressor suppressor = new FeatureFlagReloadAlertSuppressor(500, clock);
        FeatureFlagReloadAlert alert = alert("reload block rate is elevated: 30%");

        FeatureFlagReloadAlertDecision first = suppressor.evaluate(alert);
        FeatureFlagReloadAlertDecision second = suppressor.evaluate(alert);

        assertTrue(first.emitted());
        assertEquals("alert emitted", first.reason());
        assertEquals(1_500, first.nextAllowedAtMillis());
        assertFalse(second.emitted());
        assertEquals("alert cooldown active", second.reason());
        assertEquals(1_500, second.nextAllowedAtMillis());
        assertEquals(1, suppressor.trackedAlertCount());
    }

    @Test
    void emitsSameAlertAgainAfterCooldownExpires() {
        ManualTimeSource clock = new ManualTimeSource(1_000);
        FeatureFlagReloadAlertSuppressor suppressor = new FeatureFlagReloadAlertSuppressor(500, clock);
        FeatureFlagReloadAlert alert = alert("reload rejection rate is critical: 60%");

        assertTrue(suppressor.evaluate(alert).emitted());
        clock.advanceMillis(500);

        FeatureFlagReloadAlertDecision afterCooldown = suppressor.evaluate(alert);

        assertTrue(afterCooldown.emitted());
        assertEquals(2_000, afterCooldown.nextAllowedAtMillis());
    }

    @Test
    void treatsDifferentAlertDetailsAsDifferentAlerts() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagReloadAlertSuppressor suppressor = new FeatureFlagReloadAlertSuppressor(1_000, clock);

        FeatureFlagReloadAlertDecision first = suppressor.evaluate(alert("warning one"));
        FeatureFlagReloadAlertDecision second = suppressor.evaluate(alert("warning two"));

        assertTrue(first.emitted());
        assertTrue(second.emitted());
        assertEquals(2, suppressor.trackedAlertCount());
    }

    @Test
    void rejectsInvalidInputs() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagReloadAlert alert = alert("warning");

        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertSuppressor(0, clock));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertSuppressor(100, null));

        FeatureFlagReloadAlertSuppressor suppressor = new FeatureFlagReloadAlertSuppressor(100, clock);
        assertThrows(IllegalArgumentException.class, () -> suppressor.evaluate(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDecision(null, false, "reason", 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDecision(alert, false, "", 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDecision(alert, false, "reason", -1));
    }

    private static FeatureFlagReloadAlert alert(String detail) {
        return FeatureFlagReloadAlert.active(FeatureFlagReloadHealthStatus.WARNING, List.of(detail));
    }
}

