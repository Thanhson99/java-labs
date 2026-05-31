package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OutboxRetryPolicyTest {

    @Test
    void calculatesExponentialBackoffDelay() {
        OutboxRetryPolicy policy = new OutboxRetryPolicy(5, 1_000, 10_000);

        assertEquals(1_000, policy.delayBeforeNextAttemptMillis(failedEventWithAttempts(0)));
        assertEquals(2_000, policy.delayBeforeNextAttemptMillis(failedEventWithAttempts(1)));
        assertEquals(4_000, policy.delayBeforeNextAttemptMillis(failedEventWithAttempts(2)));
        assertEquals(8_000, policy.delayBeforeNextAttemptMillis(failedEventWithAttempts(3)));
        assertEquals(10_000, policy.delayBeforeNextAttemptMillis(failedEventWithAttempts(4)));
    }

    @Test
    void stopsRetryingAfterMaximumAttemptsOrPublishedStatus() {
        OutboxRetryPolicy policy = new OutboxRetryPolicy(3, 1_000, 10_000);

        assertTrue(policy.canRetry(failedEventWithAttempts(2)));
        assertFalse(policy.canRetry(failedEventWithAttempts(3)));
        assertFalse(policy.canRetry(publishedEvent()));
        assertEquals(-1, policy.delayBeforeNextAttemptMillis(failedEventWithAttempts(3)));
    }

    @Test
    void plannerBuildsRetryPlanOnlyForFailedEvents() {
        OutboxRetryPolicy policy = new OutboxRetryPolicy(3, 1_000, 10_000);
        OutboxRetryPlanner planner = new OutboxRetryPlanner(policy);

        List<OutboxRetryPlan> plans = planner.planRetries(List.of(
                failedEventWithAttempts(1),
                publishedEvent(),
                pendingEvent()
        ));

        assertEquals(List.of(new OutboxRetryPlan("event-failed-1", true, 2_000)), plans);
    }

    private static OutboxEvent failedEventWithAttempts(int attemptCount) {
        return new OutboxEvent(
                "event-failed-" + attemptCount,
                "UserRegistered",
                "u-1",
                "alice@example.com",
                OutboxEventStatus.FAILED,
                attemptCount
        );
    }

    private static OutboxEvent publishedEvent() {
        return new OutboxEvent(
                "event-published",
                "UserRegistered",
                "u-1",
                "alice@example.com",
                OutboxEventStatus.PUBLISHED,
                1
        );
    }

    private static OutboxEvent pendingEvent() {
        return new OutboxEvent(
                "event-pending",
                "UserRegistered",
                "u-1",
                "alice@example.com",
                OutboxEventStatus.PENDING,
                0
        );
    }
}
