package com.example.javalabs.basic;

import java.util.List;

/**
 * Event emitted when a user profile changed.
 *
 * @param userId changed user id
 * @param changes changed fields included in the event payload
 */
public record UserProfileChangeEvent(String userId, List<FieldChange> changes) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when {@code userId} is blank or changes are missing
     */
    public UserProfileChangeEvent {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (changes == null) {
            throw new IllegalArgumentException("changes must not be null");
        }
        changes = List.copyOf(changes);
        if (changes.isEmpty()) {
            throw new IllegalArgumentException("changes must not be empty");
        }
    }
}
