package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation that records outbound messages for assertions in tests.
 */
public final class InMemoryNotificationClient implements NotificationClient {

    private final List<String> sentMessages = new ArrayList<>();

    /**
     * Records a deterministic welcome-message payload instead of sending a real network request.
     *
     * @param userProfile user profile to include in the recorded message
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        sentMessages.add("WELCOME:" + userProfile.userId() + ":" + userProfile.email());
    }

    /**
     * Returns an immutable snapshot of recorded messages.
     *
     * @return sent welcome-message payloads in call order
     */
    public List<String> sentMessages() {
        return List.copyOf(sentMessages);
    }
}
