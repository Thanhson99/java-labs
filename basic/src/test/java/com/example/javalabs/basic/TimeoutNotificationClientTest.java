package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeoutNotificationClientTest {

    private final UserProfile userProfile = new UserProfile("u-1", "alice@example.com", Region.APAC);

    @Test
    void allowsCallThatFinishesBeforeTimeout() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryNotificationClient realClient = new InMemoryNotificationClient();
        LatencySimulatingNotificationClient fastClient =
                new LatencySimulatingNotificationClient(realClient, timeSource, 40);
        TimeoutNotificationClient client = new TimeoutNotificationClient(fastClient, 100, timeSource);

        client.sendWelcomeMessage(userProfile);

        assertEquals(1, realClient.sentMessages().size());
    }

    @Test
    void rejectsCallThatExceedsTimeout() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryNotificationClient realClient = new InMemoryNotificationClient();
        LatencySimulatingNotificationClient slowClient =
                new LatencySimulatingNotificationClient(realClient, timeSource, 150);
        TimeoutNotificationClient client = new TimeoutNotificationClient(slowClient, 100, timeSource);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        assertEquals(1, realClient.sentMessages().size());
    }

    @Test
    void propagatesDelegateFailure() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        FlakyNotificationClient failingClient = new FlakyNotificationClient(1);
        TimeoutNotificationClient client = new TimeoutNotificationClient(failingClient, 100, timeSource);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        assertEquals(1, failingClient.attemptCount());
    }
}
