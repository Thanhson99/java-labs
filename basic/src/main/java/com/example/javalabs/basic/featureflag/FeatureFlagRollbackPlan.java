package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Rollback plan generated from one feature flag audit event.
 *
 * @param auditTimestampMillis source audit event timestamp
 * @param actions rollback actions to apply or review
 */
public record FeatureFlagRollbackPlan(long auditTimestampMillis, List<FeatureFlagRollbackAction> actions) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when timestamp or actions are invalid
     */
    public FeatureFlagRollbackPlan {
        if (auditTimestampMillis < 0) {
            throw new IllegalArgumentException("auditTimestampMillis must not be negative");
        }
        if (actions == null) {
            throw new IllegalArgumentException("actions must not be null");
        }
        actions = List.copyOf(actions);
    }

    /**
     * @return {@code true} when at least one rollback action exists
     */
    public boolean hasActions() {
        return !actions.isEmpty();
    }

    /**
     * @return number of rollback actions
     */
    public int actionCount() {
        return actions.size();
    }
}
