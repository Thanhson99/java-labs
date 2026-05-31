package com.example.javalabs.basic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bulkhead decorator for {@link NotificationClient}.
 *
 * <p>A bulkhead limits how many calls may be in flight at the same time. This protects the rest of
 * the application when a downstream dependency becomes slow and callers start piling up.</p>
 */
public final class BulkheadNotificationClient implements NotificationClient {

    private final NotificationClient delegate;
    private final int maxConcurrentCalls;
    private final AtomicInteger inFlightCalls = new AtomicInteger();

    /**
     * Creates a bulkhead around a notification client.
     *
     * @param delegate downstream notification client
     * @param maxConcurrentCalls maximum number of calls allowed at the same time
     * @throws IllegalArgumentException when {@code delegate} is {@code null} or the limit is invalid
     */
    public BulkheadNotificationClient(NotificationClient delegate, int maxConcurrentCalls) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (maxConcurrentCalls <= 0) {
            throw new IllegalArgumentException("maxConcurrentCalls must be positive");
        }
        this.delegate = delegate;
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    /**
     * Sends a welcome message only when a bulkhead slot is available.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     * @throws IllegalStateException when the bulkhead is full
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        int current = inFlightCalls.incrementAndGet();
        if (current > maxConcurrentCalls) {
            inFlightCalls.decrementAndGet();
            throw new IllegalStateException("bulkhead is full");
        }

        try {
            delegate.sendWelcomeMessage(userProfile);
        } finally {
            // Always release the slot, even when the downstream client throws.
            inFlightCalls.decrementAndGet();
        }
    }

    /**
     * @return number of calls currently inside the bulkhead
     */
    public int inFlightCalls() {
        return inFlightCalls.get();
    }
}
