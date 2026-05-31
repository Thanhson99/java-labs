package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JitteredBackoffPolicyTest {

    @Test
    void keepsExponentialBackoffWhenJitterIsDisabled() {
        JitteredBackoffPolicy policy = new JitteredBackoffPolicy(5, 1_000, 10_000, 0, () -> 0.5);

        assertEquals(1_000, policy.delayBeforeNextAttemptMillis(0));
        assertEquals(2_000, policy.delayBeforeNextAttemptMillis(1));
        assertEquals(4_000, policy.delayBeforeNextAttemptMillis(2));
        assertEquals(8_000, policy.delayBeforeNextAttemptMillis(3));
        assertEquals(10_000, policy.delayBeforeNextAttemptMillis(4));
    }

    @Test
    void spreadsDelayInsideJitterRange() {
        AtomicInteger index = new AtomicInteger();
        double[] samples = {0, 0.5, 1};
        JitteredBackoffPolicy policy = new JitteredBackoffPolicy(
                5,
                1_000,
                10_000,
                0.2,
                () -> samples[index.getAndIncrement()]
        );

        assertEquals(800, policy.delayBeforeNextAttemptMillis(0));
        assertEquals(2_000, policy.delayBeforeNextAttemptMillis(1));
        assertEquals(4_800, policy.delayBeforeNextAttemptMillis(2));
    }

    @Test
    void capsJitteredDelayAtMaximumDelay() {
        JitteredBackoffPolicy policy = new JitteredBackoffPolicy(5, 1_000, 10_000, 0.5, () -> 1);

        assertEquals(10_000, policy.delayBeforeNextAttemptMillis(4));
    }

    @Test
    void stopsAfterMaximumAttempts() {
        JitteredBackoffPolicy policy = new JitteredBackoffPolicy(3, 1_000, 10_000, 0.2, () -> 0.5);

        assertTrue(policy.canRetry(2));
        assertFalse(policy.canRetry(3));
        assertEquals(-1, policy.delayBeforeNextAttemptMillis(3));
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new JitteredBackoffPolicy(0, 1_000, 10_000, 0.2));
        assertThrows(IllegalArgumentException.class, () -> new JitteredBackoffPolicy(3, 0, 10_000, 0.2));
        assertThrows(IllegalArgumentException.class, () -> new JitteredBackoffPolicy(3, 1_000, 999, 0.2));
        assertThrows(IllegalArgumentException.class, () -> new JitteredBackoffPolicy(3, 1_000, 10_000, 1.1));
    }
}
