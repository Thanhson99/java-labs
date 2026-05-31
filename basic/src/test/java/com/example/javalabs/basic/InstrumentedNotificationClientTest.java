package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstrumentedNotificationClientTest {

    private final UserProfile userProfile = new UserProfile("u-1", "alice@example.com", Region.APAC);

    @Test
    void recordsSuccessfulCallsAndAverageDuration() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        InMemoryNotificationClient realClient = new InMemoryNotificationClient();
        LatencySimulatingNotificationClient slowClient =
                new LatencySimulatingNotificationClient(realClient, timeSource, 75);
        InstrumentedNotificationClient instrumentedClient =
                new InstrumentedNotificationClient(slowClient, timeSource);

        instrumentedClient.sendWelcomeMessage(userProfile);
        instrumentedClient.sendWelcomeMessage(new UserProfile("u-2", "bob@example.com", Region.EU));

        ClientCallMetrics metrics = instrumentedClient.metrics();
        assertEquals(2, metrics.totalCalls());
        assertEquals(2, metrics.successfulCalls());
        assertEquals(0, metrics.failedCalls());
        assertEquals(150, metrics.totalDurationMillis());
        assertEquals(75.0, metrics.averageDurationMillis());
    }

    @Test
    void recordsFailedCallsAndStillMeasuresDuration() {
        ManualTimeSource timeSource = new ManualTimeSource(1_000);
        FlakyNotificationClient flakyClient = new FlakyNotificationClient(1);
        LatencySimulatingNotificationClient slowClient =
                new LatencySimulatingNotificationClient(flakyClient, timeSource, 40);
        InstrumentedNotificationClient instrumentedClient =
                new InstrumentedNotificationClient(slowClient, timeSource);

        assertThrows(IllegalStateException.class, () -> instrumentedClient.sendWelcomeMessage(userProfile));
        instrumentedClient.sendWelcomeMessage(userProfile);

        ClientCallMetrics metrics = instrumentedClient.metrics();
        assertEquals(2, metrics.totalCalls());
        assertEquals(1, metrics.successfulCalls());
        assertEquals(1, metrics.failedCalls());
        assertEquals(80, metrics.totalDurationMillis());
    }
}
