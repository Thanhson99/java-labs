package com.example.demo.messaging;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory tracker used to inspect how many events each transport has consumed.
 */
@Component
public class EventConsumptionTracker {

    private final ConcurrentHashMap<String, Integer> consumedCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> lastConsumedUserIds = new ConcurrentHashMap<>();

    /**
     * Records that a transport consumed a user registration event.
     *
     * @param transport transport identifier
     * @param event consumed event
     */
    public void record(String transport, UserRegisteredEvent event) {
        consumedCounts.merge(transport, 1, Integer::sum);
        lastConsumedUserIds.put(transport, event.userId());
    }

    /**
     * Returns a snapshot of consumed counts.
     *
     * @return transport to count map
     */
    public Map<String, Integer> consumedCounts() {
        return Map.copyOf(consumedCounts);
    }

    /**
     * Returns the most recent user id processed by each transport.
     *
     * @return transport to last user id map
     */
    public Map<String, String> lastConsumedUserIds() {
        return Map.copyOf(lastConsumedUserIds);
    }
}
