package com.example.javalabs.basic.autoscaling;

import com.example.javalabs.basic.TimeSource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Bounded in-memory audit log for autoscaling decisions.
 *
 * <p>Operational systems need enough recent history to explain why capacity changed. Keeping that
 * history bounded prevents diagnostics from becoming an unbounded memory leak during long-running
 * services.</p>
 */
public final class AutoscalingDecisionLog {

    private final int capacity;
    private final TimeSource timeSource;
    private final ArrayDeque<AutoscalingDecisionEvent> events = new ArrayDeque<>();
    private long droppedEvents;

    /**
     * Creates a bounded autoscaling decision log.
     *
     * @param capacity maximum number of retained events
     * @param timeSource clock used to timestamp decisions
     * @throws IllegalArgumentException when capacity or clock is invalid
     */
    public AutoscalingDecisionLog(int capacity, TimeSource timeSource) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.capacity = capacity;
        this.timeSource = timeSource;
    }

    /**
     * Records a decision and evicts the oldest event when the log is full.
     *
     * @param decision decision to retain
     * @return timestamped event stored in the log
     * @throws IllegalArgumentException when {@code decision} is {@code null}
     */
    public AutoscalingDecisionEvent record(AutoscalingDecision decision) {
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
        if (events.size() == capacity) {
            events.removeFirst();
            droppedEvents++;
        }
        AutoscalingDecisionEvent event =
                new AutoscalingDecisionEvent(timeSource.currentTimeMillis(), decision);
        events.addLast(event);
        return event;
    }

    /**
     * Returns retained events in insertion order.
     *
     * @return immutable copy of retained decision events
     */
    public List<AutoscalingDecisionEvent> findAll() {
        return List.copyOf(new ArrayList<>(events));
    }

    /**
     * Builds summary counters from retained decision events.
     *
     * @return decision log summary
     */
    public AutoscalingDecisionLogSummary summarize() {
        int scaleOutCount = 0;
        int scaleInCount = 0;
        int holdCount = 0;
        for (AutoscalingDecisionEvent event : events) {
            if (event.decision().action() == ScalingAction.SCALE_OUT) {
                scaleOutCount++;
            } else if (event.decision().action() == ScalingAction.SCALE_IN) {
                scaleInCount++;
            } else {
                holdCount++;
            }
        }
        return new AutoscalingDecisionLogSummary(
                events.size(),
                scaleOutCount,
                scaleInCount,
                holdCount,
                droppedEvents
        );
    }

    /**
     * Returns the configured maximum retained events.
     *
     * @return log capacity
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns how many events were evicted from the bounded log.
     *
     * @return dropped event count
     */
    public long droppedEvents() {
        return droppedEvents;
    }
}
