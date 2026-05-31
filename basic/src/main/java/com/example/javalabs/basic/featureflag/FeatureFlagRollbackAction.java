package com.example.javalabs.basic.featureflag;

/**
 * One planned rollback action for a changed feature flag.
 *
 * @param flagName changed flag name
 * @param actionType rollback action type
 * @param note human-readable note for the operator
 */
public record FeatureFlagRollbackAction(
        String flagName,
        FeatureFlagRollbackActionType actionType,
        String note) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when any field is invalid
     */
    public FeatureFlagRollbackAction {
        if (flagName == null || flagName.isBlank()) {
            throw new IllegalArgumentException("flagName must not be blank");
        }
        if (actionType == null) {
            throw new IllegalArgumentException("actionType must not be null");
        }
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException("note must not be blank");
        }
    }
}
