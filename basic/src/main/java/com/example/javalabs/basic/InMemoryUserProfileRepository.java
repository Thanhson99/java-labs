package com.example.javalabs.basic;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository used to model a single database instance.
 */
public final class InMemoryUserProfileRepository implements UserProfileRepository {

    private final String databaseName;
    private final Map<String, UserProfile> storage = new LinkedHashMap<>();

    /**
     * Creates an in-memory repository with a display name.
     *
     * @param databaseName logical database name used by routing examples
     * @throws IllegalArgumentException when {@code databaseName} is blank
     */
    public InMemoryUserProfileRepository(String databaseName) {
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalArgumentException("databaseName must not be blank");
        }
        this.databaseName = databaseName;
    }

    /**
     * @return logical database name used in tests and demos
     */
    public String databaseName() {
        return databaseName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        storage.put(userProfile.userId(), userProfile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserProfile> findById(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        return Optional.ofNullable(storage.get(userId));
    }

    /**
     * @return number of stored profiles
     */
    public int size() {
        return storage.size();
    }
}
