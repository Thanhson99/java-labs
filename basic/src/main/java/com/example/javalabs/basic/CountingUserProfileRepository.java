package com.example.javalabs.basic;

import java.util.Optional;

/**
 * Repository decorator that counts save calls so write optimizations are visible in tests.
 */
public final class CountingUserProfileRepository implements UserProfileRepository {

    private final UserProfileRepository delegate;
    private int saveCount;

    /**
     * Creates a counting decorator around a repository.
     *
     * @param delegate repository to wrap
     * @throws IllegalArgumentException when {@code delegate} is {@code null}
     */
    public CountingUserProfileRepository(UserProfileRepository delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(UserProfile userProfile) {
        saveCount++;
        delegate.save(userProfile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserProfile> findById(String userId) {
        return delegate.findById(userId);
    }

    /**
     * @return number of save calls forwarded to the delegate
     */
    public int saveCount() {
        return saveCount;
    }
}
