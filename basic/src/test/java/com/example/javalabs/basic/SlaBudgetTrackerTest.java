package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlaBudgetTrackerTest {

    @Test
    void calculatesAvailabilityAndRemainingBudget() {
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.80, 1_000, new ManualTimeSource(0));

        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.FAILURE);

        SlaBudgetSnapshot snapshot = tracker.snapshot();
        assertEquals(5, snapshot.totalCalls());
        assertEquals(1, snapshot.failedCalls());
        assertEquals(1, snapshot.allowedFailures());
        assertEquals(0.80, snapshot.availability());
        assertEquals(0, snapshot.remainingFailureBudget());
        assertFalse(snapshot.budgetExhausted());
    }

    @Test
    void marksBudgetExhaustedWhenFailuresExceedAllowedFailures() {
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.80, 1_000, new ManualTimeSource(0));

        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.FAILURE);
        tracker.record(ServiceCallOutcome.FAILURE);

        SlaBudgetSnapshot snapshot = tracker.snapshot();
        assertEquals(2, snapshot.failedCalls());
        assertTrue(snapshot.budgetExhausted());
        assertEquals(0.60, snapshot.availability());
        assertEquals(0.40, snapshot.errorRate());
    }

    @Test
    void evictsCallsOutsideRollingWindow() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.50, 1_000, timeSource);

        tracker.record(ServiceCallOutcome.FAILURE);
        timeSource.advanceMillis(999);
        tracker.record(ServiceCallOutcome.SUCCESS);
        assertEquals(2, tracker.snapshot().totalCalls());

        timeSource.advanceMillis(1);
        SlaBudgetSnapshot snapshot = tracker.snapshot();
        assertEquals(1, snapshot.totalCalls());
        assertEquals(0, snapshot.failedCalls());
        assertEquals(1.0, snapshot.availability());
    }

    @Test
    void emptySnapshotIsHealthy() {
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.99, 1_000, new ManualTimeSource(0));

        SlaBudgetSnapshot snapshot = tracker.snapshot();

        assertEquals(0, snapshot.totalCalls());
        assertEquals(1.0, snapshot.availability());
        assertEquals(0.0, snapshot.errorRate());
        assertFalse(snapshot.budgetExhausted());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new SlaBudgetTracker(0, 1_000, new ManualTimeSource(0)));
        assertThrows(IllegalArgumentException.class, () -> new SlaBudgetTracker(1.1, 1_000, new ManualTimeSource(0)));
        assertThrows(IllegalArgumentException.class, () -> new SlaBudgetTracker(0.99, 0, new ManualTimeSource(0)));
        assertThrows(IllegalArgumentException.class, () -> new SlaBudgetTracker(0.99, 1_000, null));

        SlaBudgetTracker tracker = new SlaBudgetTracker(0.99, 1_000, new ManualTimeSource(0));
        assertThrows(IllegalArgumentException.class, () -> tracker.record(null));
    }
}
