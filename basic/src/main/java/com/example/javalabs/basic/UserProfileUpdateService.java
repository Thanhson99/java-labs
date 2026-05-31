package com.example.javalabs.basic;

/**
 * Updates profile data only when the requested value differs from the stored value.
 */
public final class UserProfileUpdateService {

    private final UserProfileRepository repository;

    /**
     * Creates a profile update service.
     *
     * @param repository repository used to read and save profiles
     * @throws IllegalArgumentException when {@code repository} is {@code null}
     */
    public UserProfileUpdateService(UserProfileRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        this.repository = repository;
    }

    /**
     * Changes a user's email when needed and skips the write when the normalized value is unchanged.
     *
     * @param userId user identifier
     * @param newEmail requested email
     * @return update result
     * @throws IllegalArgumentException when {@code userId} or {@code newEmail} is blank
     * @throws IllegalStateException when the user does not exist
     */
    public ProfileUpdateResult updateEmailIfChanged(String userId, String newEmail) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("newEmail must not be blank");
        }

        UserProfile current = repository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("user not found: " + userId));
        String normalizedEmail = newEmail.trim().toLowerCase();
        if (current.email().equals(normalizedEmail)) {
            return new ProfileUpdateResult(false, "email unchanged");
        }

        // Write only when the normalized value is different, which avoids no-op database writes.
        repository.save(new UserProfile(current.userId(), normalizedEmail, current.region()));
        return new ProfileUpdateResult(true, "email updated");
    }
}
