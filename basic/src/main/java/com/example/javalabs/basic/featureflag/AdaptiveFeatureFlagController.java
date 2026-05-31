package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.SlaBudgetSnapshot;
import com.example.javalabs.basic.SlaBudgetTracker;

/**
 * Evaluates feature flags while reducing rollout when SLA budget is exhausted.
 *
 * <p>The controller demonstrates a production-oriented pattern: connect runtime health signals to
 * feature rollout decisions. When the service is healthy, the original rule is evaluated. When the
 * SLA budget is exhausted, the rule is evaluated with a lower rollout percentage to reduce load.</p>
 */
public final class AdaptiveFeatureFlagController {

    private final FeatureFlagEvaluator evaluator;
    private final SlaBudgetTracker slaBudgetTracker;
    private final int degradedRolloutPercentage;

    /**
     * Creates a controller that can degrade feature rollout based on SLA budget state.
     *
     * @param evaluator deterministic feature flag evaluator
     * @param slaBudgetTracker rolling SLA/error-budget tracker
     * @param degradedRolloutPercentage maximum rollout percentage allowed during degraded mode
     * @throws IllegalArgumentException when a dependency is {@code null} or the percentage is outside 0..100
     */
    public AdaptiveFeatureFlagController(
            FeatureFlagEvaluator evaluator,
            SlaBudgetTracker slaBudgetTracker,
            int degradedRolloutPercentage) {
        if (evaluator == null) {
            throw new IllegalArgumentException("evaluator must not be null");
        }
        if (slaBudgetTracker == null) {
            throw new IllegalArgumentException("slaBudgetTracker must not be null");
        }
        if (degradedRolloutPercentage < 0 || degradedRolloutPercentage > 100) {
            throw new IllegalArgumentException("degradedRolloutPercentage must be between 0 and 100");
        }
        this.evaluator = evaluator;
        this.slaBudgetTracker = slaBudgetTracker;
        this.degradedRolloutPercentage = degradedRolloutPercentage;
    }

    /**
     * Evaluates a feature flag rule for a user and optionally applies degraded rollout.
     *
     * @param rule feature flag rule to evaluate
     * @param userId stable user identifier used by percentage rollout hashing
     * @return evaluation result that explains whether the normal or degraded rule was used
     * @throws IllegalArgumentException when {@code rule} is {@code null}
     */
    public FeatureFlagEvaluation evaluate(FeatureFlagRule rule, String userId) {
        if (rule == null) {
            throw new IllegalArgumentException("rule must not be null");
        }
        if (!rule.enabled()) {
            return evaluator.evaluate(rule, userId);
        }

        SlaBudgetSnapshot snapshot = slaBudgetTracker.snapshot();
        if (!snapshot.budgetExhausted()) {
            return evaluator.evaluate(rule, userId);
        }

        // Preserve the original rule and evaluate a temporary lower-rollout rule for this request.
        FeatureFlagRule degradedRule = new FeatureFlagRule(
                rule.flagName(),
                rule.enabled(),
                Math.min(rule.rolloutPercentage(), degradedRolloutPercentage)
        );
        FeatureFlagEvaluation evaluation = evaluator.evaluate(degradedRule, userId);
        if (evaluation.enabled()) {
            return new FeatureFlagEvaluation(true, evaluation.bucket(), "user inside degraded rollout");
        }
        return new FeatureFlagEvaluation(false, evaluation.bucket(), "sla budget exhausted");
    }
}


