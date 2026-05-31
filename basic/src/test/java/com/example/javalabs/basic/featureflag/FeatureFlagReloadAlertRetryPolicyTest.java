package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertRetryPolicyTest {

    @Test
    void schedulesRetryWithExponentialBackoff() {
        ManualTimeSource clock = new ManualTimeSource(1_000);
        FeatureFlagReloadAlertRetryPolicy policy =
                new FeatureFlagReloadAlertRetryPolicy(4, 100, 2.0, clock);

        FeatureFlagReloadAlertRetryPlan secondAttempt = policy.planFailure(delivery(), 1);
        FeatureFlagReloadAlertRetryPlan thirdAttempt = policy.planFailure(delivery(), 2);

        assertTrue(secondAttempt.retryLater());
        assertEquals(2, secondAttempt.attempt());
        assertEquals(1_100, secondAttempt.nextAttemptAtMillis());
        assertEquals("retry alert delivery later", secondAttempt.reason());
        assertTrue(thirdAttempt.retryLater());
        assertEquals(3, thirdAttempt.attempt());
        assertEquals(1_200, thirdAttempt.nextAttemptAtMillis());
    }

    @Test
    void givesUpWhenMaxAttemptsAreExhausted() {
        ManualTimeSource clock = new ManualTimeSource(5_000);
        FeatureFlagReloadAlertRetryPolicy policy =
                new FeatureFlagReloadAlertRetryPolicy(3, 100, 2.0, clock);

        FeatureFlagReloadAlertRetryPlan plan = policy.planFailure(delivery(), 3);

        assertTrue(plan.giveUp());
        assertEquals(FeatureFlagReloadAlertRetryDecision.GIVE_UP, plan.decision());
        assertEquals(3, plan.attempt());
        assertEquals(5_000, plan.nextAttemptAtMillis());
        assertEquals("max alert delivery attempts exhausted", plan.reason());
    }

    @Test
    void supportsConstantDelayWhenMultiplierIsOne() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagReloadAlertRetryPolicy policy =
                new FeatureFlagReloadAlertRetryPolicy(3, 250, 1.0, clock);

        FeatureFlagReloadAlertRetryPlan plan = policy.planFailure(delivery(), 2);

        assertTrue(plan.retryLater());
        assertEquals(250, plan.nextAttemptAtMillis());
    }

    @Test
    void rejectsInvalidInputs() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagReloadAlertDelivery delivery = delivery();

        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertRetryPolicy(0, 100, 2.0, clock));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertRetryPolicy(3, 0, 2.0, clock));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertRetryPolicy(3, 100, 0.5, clock));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertRetryPolicy(3, 100, 2.0, null));

        FeatureFlagReloadAlertRetryPolicy policy =
                new FeatureFlagReloadAlertRetryPolicy(3, 100, 2.0, clock);
        assertThrows(IllegalArgumentException.class, () -> policy.planFailure(null, 1));
        assertThrows(IllegalArgumentException.class, () -> policy.planFailure(delivery, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertRetryPlan(null, 1, 0, "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertRetryPlan(FeatureFlagReloadAlertRetryDecision.RETRY_LATER, 0, 0, "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertRetryPlan(FeatureFlagReloadAlertRetryDecision.RETRY_LATER, 1, -1, "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertRetryPlan(FeatureFlagReloadAlertRetryDecision.RETRY_LATER, 1, 0, ""));
    }

    private static FeatureFlagReloadAlertDelivery delivery() {
        return new FeatureFlagReloadAlertDelivery(
                FeatureFlagReloadAlertChannel.ON_CALL,
                FeatureFlagReloadHealthStatus.CRITICAL,
                "feature flag reload workflow needs attention",
                List.of("reload rejection rate is critical: 60%")
        );
    }
}

