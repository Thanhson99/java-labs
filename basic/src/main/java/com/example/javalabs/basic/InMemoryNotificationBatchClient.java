package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory batch client that records batch calls and sent messages for tests.
 */
public final class InMemoryNotificationBatchClient implements NotificationBatchClient {

    private final List<List<String>> sentBatches = new ArrayList<>();

    /**
     * Records one batch of welcome messages.
     *
     * @param userProfiles users to notify
     * @throws IllegalArgumentException when {@code userProfiles} is {@code null}, empty, or contains {@code null}
     */
    @Override
    public void sendWelcomeMessages(List<UserProfile> userProfiles) {
        if (userProfiles == null || userProfiles.isEmpty()) {
            throw new IllegalArgumentException("userProfiles must not be empty");
        }
        if (userProfiles.stream().anyMatch(userProfile -> userProfile == null)) {
            throw new IllegalArgumentException("userProfiles must not contain null");
        }
        List<String> batch = userProfiles.stream()
                .map(userProfile -> "WELCOME:" + userProfile.userId() + ":" + userProfile.email())
                .toList();
        sentBatches.add(batch);
    }

    /**
     * @return number of batch calls recorded
     */
    public int batchCallCount() {
        return sentBatches.size();
    }

    /**
     * @return total number of messages recorded across all batches
     */
    public int sentMessageCount() {
        return sentBatches.stream()
                .mapToInt(List::size)
                .sum();
    }

    /**
     * @return immutable snapshot of sent batches
     */
    public List<List<String>> sentBatches() {
        return sentBatches.stream()
                .map(List::copyOf)
                .toList();
    }
}
