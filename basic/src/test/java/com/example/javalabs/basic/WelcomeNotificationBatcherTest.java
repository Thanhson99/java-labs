package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WelcomeNotificationBatcherTest {

    @Test
    void automaticallyFlushesWhenBatchIsFull() {
        InMemoryNotificationBatchClient batchClient = new InMemoryNotificationBatchClient();
        WelcomeNotificationBatcher batcher = new WelcomeNotificationBatcher(batchClient, 2);

        batcher.enqueueWelcomeMessage(user("u-1"));
        assertEquals(1, batcher.bufferedCount());
        assertEquals(0, batchClient.batchCallCount());

        batcher.enqueueWelcomeMessage(user("u-2"));

        assertEquals(0, batcher.bufferedCount());
        assertEquals(1, batchClient.batchCallCount());
        assertEquals(2, batchClient.sentMessageCount());
    }

    @Test
    void manualFlushSendsPartialBatch() {
        InMemoryNotificationBatchClient batchClient = new InMemoryNotificationBatchClient();
        WelcomeNotificationBatcher batcher = new WelcomeNotificationBatcher(batchClient, 3);

        batcher.enqueueWelcomeMessage(user("u-1"));
        batcher.enqueueWelcomeMessage(user("u-2"));
        batcher.flush();

        assertEquals(0, batcher.bufferedCount());
        assertEquals(1, batchClient.batchCallCount());
        assertEquals(2, batchClient.sentMessageCount());
    }

    @Test
    void batchingReducesDownstreamRoundTrips() {
        InMemoryNotificationBatchClient batchClient = new InMemoryNotificationBatchClient();
        WelcomeNotificationBatcher batcher = new WelcomeNotificationBatcher(batchClient, 2);

        batcher.enqueueWelcomeMessage(user("u-1"));
        batcher.enqueueWelcomeMessage(user("u-2"));
        batcher.enqueueWelcomeMessage(user("u-3"));
        batcher.enqueueWelcomeMessage(user("u-4"));
        batcher.enqueueWelcomeMessage(user("u-5"));
        batcher.flush();

        assertEquals(5, batchClient.sentMessageCount());
        assertEquals(3, batchClient.batchCallCount());
    }

    private static UserProfile user(String userId) {
        return new UserProfile(userId, userId + "@example.com", Region.APAC);
    }
}
