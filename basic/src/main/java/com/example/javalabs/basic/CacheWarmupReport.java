package com.example.javalabs.basic;

import java.util.List;

/**
 * Result of a cache warmup run.
 *
 * @param requestedIds unique ids requested for warmup
 * @param loadedIds ids successfully loaded into the cache
 * @param missingIds ids that were requested but not found
 */
public record CacheWarmupReport(
        List<String> requestedIds,
        List<String> loadedIds,
        List<String> missingIds) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when any list is {@code null}
     */
    public CacheWarmupReport {
        if (requestedIds == null) {
            throw new IllegalArgumentException("requestedIds must not be null");
        }
        if (loadedIds == null) {
            throw new IllegalArgumentException("loadedIds must not be null");
        }
        if (missingIds == null) {
            throw new IllegalArgumentException("missingIds must not be null");
        }
        requestedIds = List.copyOf(requestedIds);
        loadedIds = List.copyOf(loadedIds);
        missingIds = List.copyOf(missingIds);
    }

    /**
     * @return number of unique ids requested for warmup
     */
    public int requestedCount() {
        return requestedIds.size();
    }

    /**
     * @return number of ids successfully loaded
     */
    public int loadedCount() {
        return loadedIds.size();
    }

    /**
     * @return number of requested ids not found
     */
    public int missingCount() {
        return missingIds.size();
    }
}
