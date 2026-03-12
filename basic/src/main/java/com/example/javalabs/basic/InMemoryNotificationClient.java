package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation that records outbound messages for assertions in tests.
 */
public final class InMemoryNotificationClient implements NotificationClient {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        sentMessages.add("WELCOME:" + userProfile.userId() + ":" + userProfile.email());
    }

    public List<String> sentMessages() {
        return List.copyOf(sentMessages);
    }
}
