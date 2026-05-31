package com.example.javalabs.basic;

/**
 * Small DTO used by the JDBC examples.
 *
 * @param userId unique identifier
 * @param email email address
 */
public record JdbcUserRecord(String userId, String email) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when user id or email is blank
     */
    public JdbcUserRecord {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
    }
}
