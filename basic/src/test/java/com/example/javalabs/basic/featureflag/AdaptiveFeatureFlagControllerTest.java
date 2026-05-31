package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.ServiceCallOutcome;
import com.example.javalabs.basic.SlaBudgetTracker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies adaptive feature flag rollout decisions under healthy and exhausted SLA budgets.
 */
class AdaptiveFeatureFlagControllerTest {

    /**
     * Healthy SLA budget should leave the original rollout rule unchanged.
     */
    @Test
    void usesOriginalRolloutWhenSlaBudgetIsHealthy() {
        FeatureFlagEvaluator evaluator = new FeatureFlagEvaluator();
        AdaptiveFeatureFlagController controller =
                new AdaptiveFeatureFlagController(evaluator, healthyTracker(), 0);

        FeatureFlagEvaluation evaluation =
                controller.evaluate(new FeatureFlagRule("new-checkout", true, 100), "u-1");

        assertTrue(evaluation.enabled());
        assertEquals("user inside rollout", evaluation.reason());
    }

    /**
     * Exhausted SLA budget should evaluate a lower rollout rule.
     */
    @Test
    void reducesRolloutWhenSlaBudgetIsExhausted() {
        FeatureFlagEvaluator evaluator = new FeatureFlagEvaluator();
        AdaptiveFeatureFlagController controller =
                new AdaptiveFeatureFlagController(evaluator, exhaustedTracker(), 0);

        FeatureFlagEvaluation evaluation =
                controller.evaluate(new FeatureFlagRule("new-checkout", true, 100), "u-1");

        assertFalse(evaluation.enabled());
        assertEquals("sla budget exhausted", evaluation.reason());
    }

    /**
     * Disabled flags should keep their original disabled reason even during degraded operation.
     */
    @Test
    void keepsDisabledFlagReasonEvenWhenSlaBudgetIsExhausted() {
        FeatureFlagEvaluator evaluator = new FeatureFlagEvaluator();
        AdaptiveFeatureFlagController controller =
                new AdaptiveFeatureFlagController(evaluator, exhaustedTracker(), 0);

        FeatureFlagEvaluation evaluation =
                controller.evaluate(new FeatureFlagRule("new-checkout", false, 100), "u-1");

        assertFalse(evaluation.enabled());
        assertEquals("flag disabled", evaluation.reason());
    }

    /**
     * Users inside the degraded rollout bucket should still receive the feature.
     */
    @Test
    void keepsUsersInsideFallbackRolloutEnabled() {
        FeatureFlagEvaluator evaluator = new FeatureFlagEvaluator();
        int bucket = evaluator.bucket("new-checkout", "u-1");
        AdaptiveFeatureFlagController controller =
                new AdaptiveFeatureFlagController(evaluator, exhaustedTracker(), bucket + 1);

        FeatureFlagEvaluation evaluation =
                controller.evaluate(new FeatureFlagRule("new-checkout", true, 100), "u-1");

        assertTrue(evaluation.enabled());
        assertEquals("user inside degraded rollout", evaluation.reason());
    }

    /**
     * Degraded rollout must never expand access beyond the original flag rule.
     */
    @Test
    void neverRaisesRolloutAboveOriginalRule() {
        FeatureFlagEvaluator evaluator = new FeatureFlagEvaluator();
        int bucket = evaluator.bucket("new-checkout", "u-1");
        AdaptiveFeatureFlagController controller =
                new AdaptiveFeatureFlagController(evaluator, exhaustedTracker(), 100);

        FeatureFlagEvaluation evaluation =
                controller.evaluate(new FeatureFlagRule("new-checkout", true, bucket), "u-1");

        assertFalse(evaluation.enabled());
        assertEquals("sla budget exhausted", evaluation.reason());
    }

    /**
     * Constructor and evaluate validation should reject invalid dependencies and inputs.
     */
    @Test
    void rejectsInvalidDependencies() {
        FeatureFlagEvaluator evaluator = new FeatureFlagEvaluator();
        SlaBudgetTracker tracker = healthyTracker();

        assertThrows(IllegalArgumentException.class, () -> new AdaptiveFeatureFlagController(null, tracker, 0));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveFeatureFlagController(evaluator, null, 0));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveFeatureFlagController(evaluator, tracker, -1));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveFeatureFlagController(evaluator, tracker, 101));

        AdaptiveFeatureFlagController controller = new AdaptiveFeatureFlagController(evaluator, tracker, 0);
        assertThrows(IllegalArgumentException.class, () -> controller.evaluate(null, "u-1"));
    }

    /**
     * Builds an SLA tracker with enough successful calls to stay inside budget.
     */
    private static SlaBudgetTracker healthyTracker() {
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.80, 1_000, new ManualTimeSource(0));
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        return tracker;
    }

    /**
     * Builds an SLA tracker with enough failures to exhaust the configured budget.
     */
    private static SlaBudgetTracker exhaustedTracker() {
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.80, 1_000, new ManualTimeSource(0));
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.FAILURE);
        tracker.record(ServiceCallOutcome.FAILURE);
        return tracker;
    }
}

