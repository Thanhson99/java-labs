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

    public InMemoryUserProfileRepository(String databaseName) {
        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalArgumentException("databaseName must not be blank");
        }
        this.databaseName = databaseName;
    }

    public String databaseName() {
        return databaseName;
    }

    @Override
    public void save(UserProfile userProfile) {
        storage.put(userProfile.userId(), userProfile);
    }

    @Override
    public Optional<UserProfile> findById(String userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    public int size() {
        return storage.size();
    }
}
