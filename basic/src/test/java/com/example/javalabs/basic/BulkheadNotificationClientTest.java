package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BulkheadNotificationClientTest {

    private final UserProfile userProfile = new UserProfile("u-1", "alice@example.com", Region.APAC);

    @Test
    void rejectsCallWhenMaximumConcurrencyIsReached() throws Exception {
        BlockingNotificationClient blockingDelegate = new BlockingNotificationClient();
        BulkheadNotificationClient client = new BulkheadNotificationClient(blockingDelegate, 1);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<?> firstCall = executor.submit(() -> client.sendWelcomeMessage(userProfile));
        assertTrue(blockingDelegate.awaitStarted(1_000));
        assertEquals(1, client.inFlightCalls());

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));
        assertEquals(1, client.inFlightCalls());

        blockingDelegate.release();
        firstCall.get(1, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertEquals(0, client.inFlightCalls());
        assertEquals(1, blockingDelegate.sentCount());
    }

    @Test
    void releasesSlotAfterDelegateFails() {
        FlakyNotificationClient failingDelegate = new FlakyNotificationClient(1);
        BulkheadNotificationClient client = new BulkheadNotificationClient(failingDelegate, 1);

        assertThrows(IllegalStateException.class, () -> client.sendWelcomeMessage(userProfile));

        assertEquals(0, client.inFlightCalls());
    }
}
