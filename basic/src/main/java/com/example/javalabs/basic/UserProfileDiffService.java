package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Compares two profile snapshots before deciding whether a write or event is needed.
 *
 * <p>Snapshot diffing avoids unnecessary writes and unnecessary downstream events. The service
 * returns a value object so callers can inspect the exact fields that changed.</p>
 */
public final class UserProfileDiffService {

    /**
     * Compares two profiles that represent the same user.
     *
     * @param before existing stored profile
     * @param after requested new profile
     * @return diff containing changed fields
     * @throws IllegalArgumentException when inputs are {@code null} or belong to different users
     */
    public UserProfileDiff diff(UserProfile before, UserProfile after) {
        if (before == null) {
            throw new IllegalArgumentException("before must not be null");
        }
        if (after == null) {
            throw new IllegalArgumentException("after must not be null");
        }
        if (!before.userId().equals(after.userId())) {
            throw new IllegalArgumentException("profiles must have the same userId");
        }

        List<FieldChange> changes = new ArrayList<>();
        addChangeIfDifferent(changes, "email", before.email(), after.email());
        addChangeIfDifferent(changes, "region", before.region().name(), after.region().name());
        return new UserProfileDiff(before.userId(), changes);
    }

    /**
     * Adds one field change only when the values differ.
     *
     * @param changes mutable change list being built
     * @param fieldName field being compared
     * @param oldValue previous value
     * @param newValue new value
     */
    private static void addChangeIfDifferent(
            List<FieldChange> changes,
            String fieldName,
            String oldValue,
            String newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            changes.add(new FieldChange(fieldName, oldValue, newValue));
        }
    }
}
