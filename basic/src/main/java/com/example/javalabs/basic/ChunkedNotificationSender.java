package com.example.javalabs.basic;

import java.util.List;

/**
 * Sends many notifications by splitting the input into bounded chunks.
 */
public final class ChunkedNotificationSender {

    private final NotificationBatchClient batchClient;
    private final int chunkSize;

    /**
     * Creates a sender that splits large inputs into fixed-size chunks.
     *
     * @param batchClient downstream batch notification client
     * @param chunkSize maximum number of users per downstream batch call
     * @throws IllegalArgumentException when dependencies or chunk size are invalid
     */
    public ChunkedNotificationSender(NotificationBatchClient batchClient, int chunkSize) {
        if (batchClient == null) {
            throw new IllegalArgumentException("batchClient must not be null");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be positive");
        }
        this.batchClient = batchClient;
        this.chunkSize = chunkSize;
    }

    /**
     * Sends every profile while keeping each downstream batch within the configured chunk size.
     *
     * @param userProfiles users to notify
     * @return number of downstream batch calls
     * @throws IllegalArgumentException when {@code userProfiles} is {@code null}
     */
    public int sendAll(List<UserProfile> userProfiles) {
        if (userProfiles == null) {
            throw new IllegalArgumentException("userProfiles must not be null");
        }

        int batchCalls = 0;
        for (List<UserProfile> chunk : BatchPartitioner.partition(userProfiles, chunkSize)) {
            // Each chunk respects the downstream batch-size contract.
            batchClient.sendWelcomeMessages(chunk);
            batchCalls++;
        }
        return batchCalls;
    }
}
