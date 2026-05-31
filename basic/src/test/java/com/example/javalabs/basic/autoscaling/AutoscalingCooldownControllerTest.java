package com.example.javalabs.basic.autoscaling;

import com.example.javalabs.basic.ManualTimeSource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies cooldown protection for autoscaling decisions.
 */
class AutoscalingCooldownControllerTest {

    /**
     * Confirms the first scaling action is allowed and starts cooldown.
     */
    @Test
    void allowsFirstScalingDecisionAndStartsCooldown() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        AutoscalingCooldownController controller = new AutoscalingCooldownController(5_000, timeSource);

        AutoscalingDecision decision = controller.apply(scaleOut(), 2);

        assertEquals(ScalingAction.SCALE_OUT, decision.action());
        assertEquals(3, decision.targetInstances());
        assertTrue(controller.coolingDown());
        assertEquals(6_000, controller.cooldownUntilMillis());
    }

    /**
     * Confirms additional scaling decisions are suppressed during cooldown.
     */
    @Test
    void suppressesScalingWhileCoolingDown() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        AutoscalingCooldownController controller = new AutoscalingCooldownController(5_000, timeSource);

        controller.apply(scaleOut(), 2);
        AutoscalingDecision suppressed = controller.apply(new AutoscalingDecision(
                ScalingAction.SCALE_IN,
                1,
                "low demand"
        ), 3);

        assertEquals(ScalingAction.HOLD, suppressed.action());
        assertEquals(3, suppressed.targetInstances());
        assertEquals("scaling cooldown active until 6000", suppressed.reason());
    }

    /**
     * Confirms scaling is allowed again after the cooldown expires.
     */
    @Test
    void allowsScalingAfterCooldownExpires() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        AutoscalingCooldownController controller = new AutoscalingCooldownController(5_000, timeSource);

        controller.apply(scaleOut(), 2);
        timeSource.advanceMillis(5_000);
        AutoscalingDecision decision = controller.apply(new AutoscalingDecision(
                ScalingAction.SCALE_IN,
                2,
                "low demand"
        ), 3);

        assertEquals(ScalingAction.SCALE_IN, decision.action());
        assertEquals(2, decision.targetInstances());
        assertEquals(11_000, controller.cooldownUntilMillis());
    }

    /**
     * Confirms hold decisions pass through and do not start cooldown.
     */
    @Test
    void holdDecisionDoesNotStartCooldown() {
        AutoscalingCooldownController controller =
                new AutoscalingCooldownController(5_000, new ManualTimeSource(1_000));
        AutoscalingDecision hold = new AutoscalingDecision(ScalingAction.HOLD, 2, "stable");

        AutoscalingDecision decision = controller.apply(hold, 2);

        assertEquals(hold, decision);
        assertFalse(controller.coolingDown());
        assertEquals(0, controller.cooldownUntilMillis());
    }

    /**
     * Confirms reset clears cooldown immediately.
     */
    @Test
    void resetClearsCooldown() {
        AutoscalingCooldownController controller =
                new AutoscalingCooldownController(5_000, new ManualTimeSource(1_000));
        controller.apply(scaleOut(), 2);

        controller.reset();

        assertFalse(controller.coolingDown());
        assertEquals(0, controller.cooldownUntilMillis());
    }

    /**
     * Documents validation for constructor and apply boundaries.
     */
    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new AutoscalingCooldownController(0, new ManualTimeSource(0)));
        assertThrows(IllegalArgumentException.class,
                () -> new AutoscalingCooldownController(1_000, null));

        AutoscalingCooldownController controller =
                new AutoscalingCooldownController(1_000, new ManualTimeSource(0));
        assertThrows(IllegalArgumentException.class, () -> controller.apply(null, 1));
        assertThrows(IllegalArgumentException.class, () -> controller.apply(scaleOut(), 0));
    }

    /**
     * Creates a reusable scale-out decision.
     */
    private static AutoscalingDecision scaleOut() {
        return new AutoscalingDecision(ScalingAction.SCALE_OUT, 3, "high load");
    }
}
