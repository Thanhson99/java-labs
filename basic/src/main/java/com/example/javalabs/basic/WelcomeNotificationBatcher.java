package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * Buffers welcome notifications and flushes them in batches.
 *
 * <p>Batching reduces the number of downstream calls while preserving explicit flush control for
 * callers that need to send a partial batch before shutdown.</p>
 */
public final class WelcomeNotificationBatcher {

    private final NotificationBatchClient batchClient;
    private final int batchSize;
    private final List<UserProfile> buffer = new ArrayList<>();

    /**
     * Creates a batcher with a downstream batch client.
     *
     * @param batchClient client that sends each completed batch
     * @param batchSize number of buffered profiles that triggers an automatic flush
     * @throws IllegalArgumentException when {@code batchClient} is null or {@code batchSize} is not
     *         positive
     */
    public WelcomeNotificationBatcher(NotificationBatchClient batchClient, int batchSize) {
        if (batchClient == null) {
            throw new IllegalArgumentException("batchClient must not be null");
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be positive");
        }
        this.batchClient = batchClient;
        this.batchSize = batchSize;
    }

    /**
     * Adds a notification to the current batch and flushes automatically when the batch is full.
     *
     * @param userProfile user to notify
     * @throws NullPointerException when {@code userProfile} is {@code null}
     */
    public void enqueueWelcomeMessage(UserProfile userProfile) {
        buffer.add(userProfile);
        if (buffer.size() >= batchSize) {
            flush();
        }
    }

    /**
     * Sends all buffered notifications now.
     *
     * <p>The buffer is copied and cleared before sending so the same profiles are not resent if a
     * caller invokes {@code flush()} again after a successful send.</p>
     */
    public void flush() {
        if (buffer.isEmpty()) {
            return;
        }
        List<UserProfile> batch = List.copyOf(buffer);
        buffer.clear();
        batchClient.sendWelcomeMessages(batch);
    }

    /**
     * Returns how many profiles are waiting in the current batch.
     *
     * @return buffered profile count
     */
    public int bufferedCount() {
        return buffer.size();
    }
}
