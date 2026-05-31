package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies batch partitioning behavior and the notification sender that uses it.
 */
class BatchPartitionerTest {

    /**
     * Confirms that partitioning preserves input order while enforcing the requested chunk size.
     */
    @Test
    void partitionsListIntoFixedSizeChunks() {
        List<List<Integer>> chunks = BatchPartitioner.partition(List.of(1, 2, 3, 4, 5), 2);

        assertEquals(List.of(
                List.of(1, 2),
                List.of(3, 4),
                List.of(5)
        ), chunks);
    }

    /**
     * Confirms that empty input produces an empty immutable result instead of a single empty chunk.
     */
    @Test
    void emptyInputReturnsNoChunks() {
        assertEquals(List.of(), BatchPartitioner.partition(List.of(), 3));
    }

    /**
     * Documents the public validation boundary for chunk size.
     */
    @Test
    void rejectsInvalidChunkSize() {
        assertThrows(IllegalArgumentException.class,
                () -> BatchPartitioner.partition(List.of(1, 2, 3), 0));
    }

    /**
     * Shows how the partitioner keeps downstream notification calls bounded.
     */
    @Test
    void chunkedSenderKeepsDownstreamBatchSizeBounded() {
        InMemoryNotificationBatchClient batchClient = new InMemoryNotificationBatchClient();
        ChunkedNotificationSender sender = new ChunkedNotificationSender(batchClient, 2);

        int calls = sender.sendAll(List.of(user("u-1"), user("u-2"), user("u-3"), user("u-4"), user("u-5")));

        assertEquals(3, calls);
        assertEquals(3, batchClient.batchCallCount());
        assertEquals(5, batchClient.sentMessageCount());
        assertEquals(2, batchClient.sentBatches().get(0).size());
        assertEquals(1, batchClient.sentBatches().get(2).size());
    }

    /**
     * Builds a minimal valid user for batch notification tests.
     */
    private static UserProfile user(String userId) {
        return new UserProfile(userId, userId + "@example.com", Region.APAC);
    }
}
