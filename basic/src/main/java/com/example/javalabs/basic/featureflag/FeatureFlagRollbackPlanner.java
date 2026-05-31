package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds an operator-readable rollback plan from feature flag audit events.
 */
public final class FeatureFlagRollbackPlanner {

    /**
     * Builds a rollback plan from one audit event.
     *
     * @param auditEvent audit event describing changed flags
     * @return rollback plan with one action per changed flag
     * @throws IllegalArgumentException when {@code auditEvent} is {@code null}
     */
    public FeatureFlagRollbackPlan plan(FeatureFlagAuditEvent auditEvent) {
        if (auditEvent == null) {
            throw new IllegalArgumentException("auditEvent must not be null");
        }

        List<FeatureFlagRollbackAction> actions = new ArrayList<>();
        for (String flagName : auditEvent.addedFlags()) {
            // Added flags can usually be rolled back by disabling or removing the new rule.
            actions.add(new FeatureFlagRollbackAction(
                    flagName,
                    FeatureFlagRollbackActionType.DISABLE_ADDED_FLAG,
                    "Disable this newly added flag or remove it from config."
            ));
        }
        for (String flagName : auditEvent.updatedFlags()) {
            // Updated flags need previous config history to restore exact rollout values.
            actions.add(new FeatureFlagRollbackAction(
                    flagName,
                    FeatureFlagRollbackActionType.RESTORE_UPDATED_FLAG,
                    "Restore the previous rule from config history."
            ));
        }
        for (String flagName : auditEvent.removedFlags()) {
            // Removed flags require re-adding the prior rule from config history.
            actions.add(new FeatureFlagRollbackAction(
                    flagName,
                    FeatureFlagRollbackActionType.RESTORE_REMOVED_FLAG,
                    "Re-add the removed rule from config history."
            ));
        }
        return new FeatureFlagRollbackPlan(auditEvent.timestampMillis(), actions);
    }
}
