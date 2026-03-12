package com.example.javalabs.basic;

/**
 * Domain model shared across repository and service examples.
 *
 * @param userId unique user identifier
 * @param email user email address
 * @param region region used for data placement and routing
 */
public record UserProfile(String userId, String email, Region region) {

    public UserProfile {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (region == null) {
            throw new IllegalArgumentException("region must not be null");
        }
    }
}
