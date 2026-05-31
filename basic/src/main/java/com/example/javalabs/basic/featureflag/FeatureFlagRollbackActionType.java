package com.example.javalabs.basic.featureflag;

/**
 * Type of rollback action for a feature flag change.
 */
public enum FeatureFlagRollbackActionType {
    /**
     * Disable or remove a flag that was introduced by the audited change.
     */
    DISABLE_ADDED_FLAG,

    /**
     * Restore the previous rule for a flag that was modified.
     */
    RESTORE_UPDATED_FLAG,

    /**
     * Re-add a flag that was removed by the audited change.
     */
    RESTORE_REMOVED_FLAG
}
