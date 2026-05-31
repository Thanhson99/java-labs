package com.example.javalabs.basic;

import java.util.Optional;

/**
 * Repository abstraction decouples business logic from storage details.
 */
public interface UserProfileRepository {

    /**
     * Persists or replaces a user profile.
     *
     * @param userProfile the profile to save
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    void save(UserProfile userProfile);

    /**
     * Looks up a user profile by its identifier.
     *
     * @param userId the user identifier
     * @return the profile when present
     * @throws IllegalArgumentException when {@code userId} is blank
     */
    Optional<UserProfile> findById(String userId);
}
