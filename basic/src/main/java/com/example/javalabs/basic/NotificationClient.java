package com.example.javalabs.basic;

/**
 * Simulates a network client used by one service to call another service.
 */
public interface NotificationClient {

    /**
     * Sends a welcome message to a new user.
     *
     * @param userProfile the user to notify
     */
    void sendWelcomeMessage(UserProfile userProfile);
}
