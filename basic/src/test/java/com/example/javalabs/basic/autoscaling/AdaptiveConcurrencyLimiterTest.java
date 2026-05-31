package com.example.javalabs.basic.autoscaling;

import com.example.javalabs.basic.ServiceCallOutcome;
import com.example.javalabs.basic.metrics.ErrorRateSnapshot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies adaptive concurrency behavior under healthy and unhealthy error-rate snapshots.
 */
class AdaptiveConcurrencyLimiterTest {

    /**
     * Confirms the limiter rejects acquisitions once the current limit is reached.
     */
    @Test
    void rejectsWhenCurrentLimitIsReached() {
        AdaptiveConcurrencyLimiter limiter = new AdaptiveConcurrencyLimiter(1, 5, 2, 1, 1, 2);

        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire());

        assertEquals(new AdaptiveConcurrencySnapshot(2, 2, 0), limiter.snapshot());
    }

    /**
     * Confirms unhealthy error-rate snapshots reduce the limit quickly.
     */
    @Test
    void decreasesLimitWhenHealthIsUnhealthy() {
        AdaptiveConcurrencyLimiter limiter = new AdaptiveConcurrencyLimiter(1, 10, 5, 2, 1, 2);
        limiter.tryAcquire();

        AdaptiveConcurrencySnapshot snapshot =
                limiter.complete(ServiceCallOutcome.FAILURE, new ErrorRateSnapshot(4, 3, 0.75, false));

        assertEquals(3, snapshot.currentLimit());
        assertEquals(0, snapshot.inFlight());
        assertEquals(0, snapshot.healthyStreak());
    }

    /**
     * Confirms healthy successes raise the limit only after a configured streak.
     */
    @Test
    void increasesLimitAfterHealthyRecoveryStreak() {
        AdaptiveConcurrencyLimiter limiter = new AdaptiveConcurrencyLimiter(1, 5, 2, 1, 2, 2);
        ErrorRateSnapshot healthy = new ErrorRateSnapshot(5, 0, 0.0, true);

        limiter.tryAcquire();
        assertEquals(2, limiter.complete(ServiceCallOutcome.SUCCESS, healthy).currentLimit());

        limiter.tryAcquire();
        AdaptiveConcurrencySnapshot snapshot = limiter.complete(ServiceCallOutcome.SUCCESS, healthy);

        assertEquals(4, snapshot.currentLimit());
        assertEquals(0, snapshot.healthyStreak());
    }

    /**
     * Confirms failed outcomes reset recovery streak even when aggregate health is still healthy.
     */
    @Test
    void failureResetsHealthyRecoveryStreak() {
        AdaptiveConcurrencyLimiter limiter = new AdaptiveConcurrencyLimiter(1, 5, 2, 1, 1, 2);
        ErrorRateSnapshot healthy = new ErrorRateSnapshot(5, 1, 0.20, true);

        limiter.tryAcquire();
        assertEquals(1, limiter.complete(ServiceCallOutcome.SUCCESS, healthy).healthyStreak());

        limiter.tryAcquire();
        AdaptiveConcurrencySnapshot snapshot = limiter.complete(ServiceCallOutcome.FAILURE, healthy);

        assertEquals(0, snapshot.healthyStreak());
        assertEquals(2, snapshot.currentLimit());
    }

    /**
     * Documents validation for configuration and completion boundaries.
     */
    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveConcurrencyLimiter(0, 1, 1, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveConcurrencyLimiter(2, 1, 1, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveConcurrencyLimiter(1, 3, 4, 1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveConcurrencyLimiter(1, 3, 1, 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveConcurrencyLimiter(1, 3, 1, 1, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveConcurrencyLimiter(1, 3, 1, 1, 1, 0));

        AdaptiveConcurrencyLimiter limiter = new AdaptiveConcurrencyLimiter(1, 3, 1, 1, 1, 1);
        assertThrows(IllegalStateException.class,
                () -> limiter.complete(ServiceCallOutcome.SUCCESS, new ErrorRateSnapshot(1, 0, 0.0, true)));
        limiter.tryAcquire();
        assertThrows(IllegalArgumentException.class,
                () -> limiter.complete(null, new ErrorRateSnapshot(1, 0, 0.0, true)));
        assertThrows(IllegalArgumentException.class,
                () -> limiter.complete(ServiceCallOutcome.SUCCESS, null));
    }
}
