package com.example.demo.notification;

import com.example.demo.profile.UserProfileEntity;

/**
 * Boundary that represents a downstream notification service.
 */
public interface NotificationGateway {

    /**
     * Sends a welcome-style notification for a newly created user.
     *
     * @param userProfile the newly registered user
     */
    void sendWelcome(UserProfileEntity userProfile);
}
