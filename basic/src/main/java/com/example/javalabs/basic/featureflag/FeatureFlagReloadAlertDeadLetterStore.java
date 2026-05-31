package com.example.javalabs.basic.featureflag;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Bounded store for alert deliveries that have exhausted retries.
 */
public final class FeatureFlagReloadAlertDeadLetterStore {

    private final int capacity;
    private final Deque<FeatureFlagReloadAlertDeadLetter> records = new ArrayDeque<>();
    private int droppedCount;

    /**
     * Creates a bounded dead-letter store.
     *
     * @param capacity maximum retained records
     * @throws IllegalArgumentException when {@code capacity} is not positive
     */
    public FeatureFlagReloadAlertDeadLetterStore(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Records a delivery only when its retry plan says to give up.
     *
     * @param delivery failed delivery payload
     * @param retryPlan retry plan from the policy
     * @return {@code true} when a dead-letter record was stored
     * @throws IllegalArgumentException when inputs are {@code null}
     */
    public boolean record(FeatureFlagReloadAlertDelivery delivery, FeatureFlagReloadAlertRetryPlan retryPlan) {
        if (delivery == null) {
            throw new IllegalArgumentException("delivery must not be null");
        }
        if (retryPlan == null) {
            throw new IllegalArgumentException("retryPlan must not be null");
        }
        if (!retryPlan.giveUp()) {
            return false;
        }
        if (records.size() == capacity) {
            // Keep memory bounded by dropping the oldest retained record first.
            records.removeFirst();
            droppedCount++;
        }
        records.addLast(new FeatureFlagReloadAlertDeadLetter(
                delivery,
                retryPlan.attempt(),
                retryPlan.nextAttemptAtMillis(),
                retryPlan.reason()
        ));
        return true;
    }

    /**
     * Returns retained dead-letter records in insertion order.
     *
     * @return immutable snapshot of retained records
     */
    public List<FeatureFlagReloadAlertDeadLetter> findAll() {
        return List.copyOf(new ArrayList<>(records));
    }

    /**
     * Removes a retained dead-letter record after successful replay.
     *
     * @param deadLetter record to remove
     * @return {@code true} when the record was present and removed
     * @throws IllegalArgumentException when {@code deadLetter} is {@code null}
     */
    public boolean remove(FeatureFlagReloadAlertDeadLetter deadLetter) {
        if (deadLetter == null) {
            throw new IllegalArgumentException("deadLetter must not be null");
        }
        return records.remove(deadLetter);
    }

    /**
     * @return number of retained records
     */
    public int size() {
        return records.size();
    }

    /**
     * @return maximum retained records
     */
    public int capacity() {
        return capacity;
    }

    /**
     * @return number of records dropped because the bounded store was full
     */
    public int droppedCount() {
        return droppedCount;
    }
}
