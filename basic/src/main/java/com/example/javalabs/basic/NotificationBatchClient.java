package com.example.javalabs.basic;

import java.util.List;

/**
 * Downstream client that can send multiple welcome notifications in one call.
 */
public interface NotificationBatchClient {

    /**
     * Sends welcome messages for a bounded batch of users.
     *
     * @param userProfiles users to notify
     * @throws IllegalArgumentException when {@code userProfiles} is {@code null}
     */
    void sendWelcomeMessages(List<UserProfile> userProfiles);
}
