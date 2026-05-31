package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CircuitBreakerNotificationClientTest {

    private final UserProfile userProfile = new UserProfile("u-1", "alice@example.com", Region.APAC);

    @Test
    void opensAfterFailureThresholdAndRejectsWithoutCallingDelegate() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        FlakyNotificationClient delegate = new FlakyNotificationClient(5);
        CircuitBreakerNotificationClient client =
                new CircuitBreakerNotificationClient(delegate, 2, 10_000, timeSource);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        assertEquals(CircuitBreakerState.OPEN, client.state());

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));

        assertEquals(2, delegate.attemptCount());
    }

    @Test
    void movesToHalfOpenAfterCooldownAndClosesAfterSuccess() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        FlakyNotificationClient delegate = new FlakyNotificationClient(2);
        CircuitBreakerNotificationClient client =
                new CircuitBreakerNotificationClient(delegate, 2, 10_000, timeSource);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));

        timeSource.advanceMillis(10_000);
        assertEquals(CircuitBreakerState.HALF_OPEN, client.state());

        client.sendWelcomeMessage(userProfile);

        assertEquals(CircuitBreakerState.CLOSED, client.state());
        assertEquals(0, client.consecutiveFailures());
        assertEquals(3, delegate.attemptCount());
    }

    @Test
    void failedHalfOpenTrialOpensAgain() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        FlakyNotificationClient delegate = new FlakyNotificationClient(10);
        CircuitBreakerNotificationClient client =
                new CircuitBreakerNotificationClient(delegate, 2, 10_000, timeSource);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        timeSource.advanceMillis(10_000);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));

        assertEquals(CircuitBreakerState.OPEN, client.state());
        assertEquals(3, delegate.attemptCount());
    }
}
