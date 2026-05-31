package com.example.javalabs.basic;

/**
 * Idempotency decorator for {@link NotificationClient}.
 *
 * <p>Retries and message redelivery can call the same side effect more than once. This wrapper
 * uses a stable operation key so duplicate calls are skipped after the first successful decision
 * to process the notification.</p>
 */
public final class IdempotentNotificationClient implements NotificationClient {

    private final NotificationClient delegate;
    private final IdempotencyStore store;

    /**
     * Creates an idempotent notification client.
     *
     * @param delegate downstream notification client
     * @param store operation-key store used to detect duplicates
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public IdempotentNotificationClient(NotificationClient delegate, IdempotencyStore store) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (store == null) {
            throw new IllegalArgumentException("store must not be null");
        }
        this.delegate = delegate;
        this.store = store;
    }

    /**
     * Sends the welcome message only once per user id.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        String key = "WELCOME:" + userProfile.userId();
        if (!store.markProcessing(key)) {
            // Duplicate work is skipped because the side effect was already accepted before.
            return;
        }
        delegate.sendWelcomeMessage(userProfile);
    }
}
