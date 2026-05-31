package com.example.javalabs.basic;

import java.util.List;

/**
 * Difference between two user profile snapshots.
 *
 * @param userId profile identifier
 * @param changes changed fields
 */
public record UserProfileDiff(String userId, List<FieldChange> changes) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when {@code userId} is blank or {@code changes} is {@code null}
     */
    public UserProfileDiff {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (changes == null) {
            throw new IllegalArgumentException("changes must not be null");
        }
        changes = List.copyOf(changes);
    }

    /**
     * @return {@code true} when at least one field changed
     */
    public boolean hasChanges() {
        return !changes.isEmpty();
    }

    /**
     * @return number of changed fields
     */
    public int changeCount() {
        return changes.size();
    }
}
