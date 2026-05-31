package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResilientNotificationClientTest {

    private final UserProfile userProfile = new UserProfile("u-1", "alice@example.com", Region.APAC);

    @Test
    void retriesTemporaryFailureAndEventuallySucceeds() {
        FlakyNotificationClient delegate = new FlakyNotificationClient(2);
        ResilientNotificationClient client = new ResilientNotificationClient(delegate, 3);

        client.sendWelcomeMessage(userProfile);

        assertEquals(3, delegate.attemptCount());
        assertEquals(1, delegate.successCount());
    }

    @Test
    void stopsAfterMaximumAttempts() {
        FlakyNotificationClient delegate = new FlakyNotificationClient(5);
        ResilientNotificationClient client = new ResilientNotificationClient(delegate, 3);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        assertEquals(3, delegate.attemptCount());
        assertEquals(0, delegate.successCount());
    }

    @Test
    void succeedsImmediatelyWhenDelegateIsHealthy() {
        FlakyNotificationClient delegate = new FlakyNotificationClient(0);
        ResilientNotificationClient client = new ResilientNotificationClient(delegate, 3);

        client.sendWelcomeMessage(userProfile);

        assertEquals(1, delegate.attemptCount());
        assertEquals(1, delegate.successCount());
    }

}
