package com.example.javalabs.basic;

/**
 * Captures failed notification side effects into a dead-letter store.
 *
 * <p>The wrapper still rethrows the original failure so the caller can decide whether the request
 * should fail, retry, or continue. The important optimization is operational: the failed work is no
 * longer invisible.</p>
 */
public final class DeadLetteringNotificationClient implements NotificationClient {

    private final NotificationClient delegate;
    private final DeadLetterStore deadLetterStore;

    /**
     * Creates a dead-lettering notification client.
     *
     * @param delegate downstream notification client
     * @param deadLetterStore store used to retain failed side effects
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public DeadLetteringNotificationClient(NotificationClient delegate, DeadLetterStore deadLetterStore) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (deadLetterStore == null) {
            throw new IllegalArgumentException("deadLetterStore must not be null");
        }
        this.delegate = delegate;
        this.deadLetterStore = deadLetterStore;
    }

    /**
     * Sends a welcome message and stores failed work for later inspection.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     * @throws RuntimeException rethrows the downstream failure after saving a dead-letter message
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        try {
            delegate.sendWelcomeMessage(userProfile);
        } catch (RuntimeException exception) {
            // Store enough context to replay or inspect the failed side effect later.
            deadLetterStore.save(new DeadLetterMessage(
                    "WELCOME:" + userProfile.userId(),
                    userProfile.email(),
                    exception.getMessage()
            ));
            throw exception;
        }
    }
}
