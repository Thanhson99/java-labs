package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagRollbackPlannerTest {

    private final FeatureFlagRollbackPlanner planner = new FeatureFlagRollbackPlanner();

    @Test
    void buildsRollbackActionsFromAuditEvent() {
        FeatureFlagAuditEvent auditEvent = new FeatureFlagAuditEvent(
                1_000,
                List.of("new-flag"),
                List.of("changed-flag"),
                List.of("removed-flag")
        );

        FeatureFlagRollbackPlan plan = planner.plan(auditEvent);

        assertEquals(1_000, plan.auditTimestampMillis());
        assertTrue(plan.hasActions());
        assertEquals(3, plan.actionCount());
        assertEquals(List.of(
                new FeatureFlagRollbackAction(
                        "new-flag",
                        FeatureFlagRollbackActionType.DISABLE_ADDED_FLAG,
                        "Disable this newly added flag or remove it from config."),
                new FeatureFlagRollbackAction(
                        "changed-flag",
                        FeatureFlagRollbackActionType.RESTORE_UPDATED_FLAG,
                        "Restore the previous rule from config history."),
                new FeatureFlagRollbackAction(
                        "removed-flag",
                        FeatureFlagRollbackActionType.RESTORE_REMOVED_FLAG,
                        "Re-add the removed rule from config history.")
        ), plan.actions());
    }

    @Test
    void emptyAuditEventProducesEmptyPlan() {
        FeatureFlagRollbackPlan plan = planner.plan(new FeatureFlagAuditEvent(
                1_000,
                List.of(),
                List.of(),
                List.of()
        ));

        assertFalse(plan.hasActions());
        assertEquals(0, plan.actionCount());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> planner.plan(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagRollbackPlan(-1, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagRollbackAction("", FeatureFlagRollbackActionType.DISABLE_ADDED_FLAG, "note"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagRollbackAction("flag", null, "note"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagRollbackAction("flag", FeatureFlagRollbackActionType.DISABLE_ADDED_FLAG, ""));
    }
}
