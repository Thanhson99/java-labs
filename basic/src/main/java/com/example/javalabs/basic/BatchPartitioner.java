package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits a large list into fixed-size chunks for batch processing.
 *
 * <p>This utility is useful when a downstream service, database, or API should receive bounded
 * batches instead of one large payload. The returned chunks are immutable snapshots, so callers can
 * safely pass them to other methods without accidental mutation.</p>
 */
public final class BatchPartitioner {

    /**
     * Utility class; instances are not needed.
     */
    private BatchPartitioner() {
    }

    /**
     * Partitions input items into immutable chunks.
     *
     * @param items items to split
     * @param chunkSize maximum number of items per chunk
     * @param <T> item type
     * @return chunks that preserve input order
     * @throws IllegalArgumentException when {@code items} is {@code null} or {@code chunkSize} is not positive
     */
    public static <T> List<List<T>> partition(List<T> items, int chunkSize) {
        if (items == null) {
            throw new IllegalArgumentException("items must not be null");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be positive");
        }

        List<List<T>> chunks = new ArrayList<>();
        for (int start = 0; start < items.size(); start += chunkSize) {
            int end = Math.min(start + chunkSize, items.size());
            // Copy each sub-list so later changes to the input list cannot change produced chunks.
            chunks.add(List.copyOf(items.subList(start, end)));
        }
        return List.copyOf(chunks);
    }
}
