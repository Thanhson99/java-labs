package com.example.javalabs.basic.featureflag;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Bounded audit log for alerts about dead-letter alert workflow health.
 *
 * <p>The log keeps recent incidents in memory and drops the oldest entry when full. That keeps the
 * example safe for long-running practice apps while still showing the production pattern: always
 * bound operational history that can grow during failures.</p>
 */
public final class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog {

    private final int capacity;
    private final Deque<FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident> incidents = new ArrayDeque<>();
    private int droppedCount;

    /**
     * Creates an incident log with fixed capacity.
     *
     * @param capacity maximum number of retained incidents
     * @throws IllegalArgumentException when {@code capacity} is not positive
     */
    public FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Records an active alert result as an incident.
     *
     * <p>Inactive alerts are intentionally ignored because they represent healthy or suppressed-warning
     * states rather than incidents worth auditing.</p>
     *
     * @param result alert pipeline result to record
     * @return {@code true} when an incident was stored; {@code false} when the alert was inactive
     * @throws IllegalArgumentException when {@code result} is {@code null}
     */
    public boolean record(FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }
        if (!result.alert().active()) {
            return false;
        }
        // Keep memory bounded by evicting the oldest incident before adding the new one.
        if (incidents.size() == capacity) {
            incidents.removeFirst();
            droppedCount++;
        }
        incidents.addLast(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident(
                result.healthReport().status(),
                result.route().channel(),
                result.delivered(),
                result.alert().message(),
                result.healthReport().warnings()
        ));
        return true;
    }

    /**
     * Returns a defensive immutable snapshot of retained incidents.
     *
     * @return retained incidents in insertion order
     */
    public List<FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident> findAll() {
        return List.copyOf(new ArrayList<>(incidents));
    }

    /**
     * @return number of incidents currently retained
     */
    public int size() {
        return incidents.size();
    }

    /**
     * @return maximum number of incidents retained by this log
     */
    public int capacity() {
        return capacity;
    }

    /**
     * @return number of incidents dropped because the log was full
     */
    public int droppedCount() {
        return droppedCount;
    }
}
