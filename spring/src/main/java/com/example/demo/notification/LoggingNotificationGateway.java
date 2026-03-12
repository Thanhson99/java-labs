package com.example.demo.notification;

import com.example.demo.profile.UserProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Demo notification adapter that logs a message instead of calling a real remote service.
 */
@Component
public class LoggingNotificationGateway implements NotificationGateway {

    private static final Logger logger = LoggerFactory.getLogger(LoggingNotificationGateway.class);

    @Override
    public void sendWelcome(UserProfileEntity userProfile) {
        logger.info("Sending welcome notification to {} ({})", userProfile.getUserId(), userProfile.getEmail());
    }
}
