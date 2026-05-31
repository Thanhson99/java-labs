package com.example.javalabs.basic;

/**
 * Learning double that fails a configured number of times before succeeding.
 */
public final class FlakyNotificationClient implements NotificationClient {

    private final int failuresBeforeSuccess;
    private int attemptCount;
    private int successCount;

    /**
     * Creates a client that fails before eventually succeeding.
     *
     * @param failuresBeforeSuccess number of initial calls that should fail
     * @throws IllegalArgumentException when {@code failuresBeforeSuccess} is negative
     */
    public FlakyNotificationClient(int failuresBeforeSuccess) {
        if (failuresBeforeSuccess < 0) {
            throw new IllegalArgumentException("failuresBeforeSuccess must not be negative");
        }
        this.failuresBeforeSuccess = failuresBeforeSuccess;
    }

    /**
     * Records an attempt and fails until the configured failure count is exhausted.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     * @throws IllegalStateException while the fake client is still configured to fail
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        attemptCount++;
        if (attemptCount <= failuresBeforeSuccess) {
            throw new IllegalStateException("temporary notification failure");
        }
        successCount++;
    }

    /**
     * @return total send attempts observed by the fake client
     */
    public int attemptCount() {
        return attemptCount;
    }

    /**
     * @return successful sends observed by the fake client
     */
    public int successCount() {
        return successCount;
    }
}
