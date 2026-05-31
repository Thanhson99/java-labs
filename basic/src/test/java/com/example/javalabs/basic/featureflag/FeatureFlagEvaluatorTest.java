package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagEvaluatorTest {

    private final FeatureFlagEvaluator evaluator = new FeatureFlagEvaluator();

    @Test
    void givesStableBucketForSameFlagAndUser() {
        int first = evaluator.bucket("new-checkout", "u-1");
        int second = evaluator.bucket("new-checkout", "u-1");

        assertEquals(first, second);
    }

    @Test
    void disablesEveryoneWhenFlagIsOff() {
        FeatureFlagRule rule = new FeatureFlagRule("new-checkout", false, 100);

        FeatureFlagEvaluation evaluation = evaluator.evaluate(rule, "u-1");

        assertFalse(evaluation.enabled());
        assertEquals("flag disabled", evaluation.reason());
    }

    @Test
    void enablesEveryoneAtFullRollout() {
        FeatureFlagRule rule = new FeatureFlagRule("new-checkout", true, 100);

        FeatureFlagEvaluation evaluation = evaluator.evaluate(rule, "u-1");

        assertTrue(evaluation.enabled());
        assertEquals("user inside rollout", evaluation.reason());
    }

    @Test
    void respectsZeroRollout() {
        FeatureFlagRule rule = new FeatureFlagRule("new-checkout", true, 0);

        FeatureFlagEvaluation evaluation = evaluator.evaluate(rule, "u-1");

        assertFalse(evaluation.enabled());
        assertEquals("rollout is zero", evaluation.reason());
    }

    @Test
    void comparesUserBucketWithRolloutPercentage() {
        int bucket = evaluator.bucket("new-checkout", "u-1");
        FeatureFlagRule includesUser = new FeatureFlagRule("new-checkout", true, bucket + 1);
        FeatureFlagRule excludesUser = new FeatureFlagRule("new-checkout", true, bucket);

        assertTrue(evaluator.evaluate(includesUser, "u-1").enabled());
        assertFalse(evaluator.evaluate(excludesUser, "u-1").enabled());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagRule("", true, 10));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagRule("flag", true, -1));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagRule("flag", true, 101));
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(null, "u-1"));
        assertThrows(IllegalArgumentException.class,
                () -> evaluator.evaluate(new FeatureFlagRule("flag", true, 10), " "));
    }
}
