package com.example.javalabs.basic;

/**
 * Simulates a network client used by one service to call another service.
 */
public interface NotificationClient {

    /**
     * Sends a welcome message to a new user.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     */
    void sendWelcomeMessage(UserProfile userProfile);
}
