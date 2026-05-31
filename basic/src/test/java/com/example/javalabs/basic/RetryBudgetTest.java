package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetryBudgetTest {

    @Test
    void allowsRetriesUntilBudgetIsExhausted() {
        RetryBudget budget = new RetryBudget(2, 1_000, new ManualTimeSource(0));

        assertTrue(budget.tryAcquireRetry());
        assertTrue(budget.tryAcquireRetry());
        assertFalse(budget.tryAcquireRetry());
        assertEquals(2, budget.usedRetries());
        assertEquals(0, budget.remainingRetries());
    }

    @Test
    void freesBudgetWhenWindowExpires() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        RetryBudget budget = new RetryBudget(2, 1_000, timeSource);

        assertTrue(budget.tryAcquireRetry());
        assertTrue(budget.tryAcquireRetry());

        timeSource.advanceMillis(999);
        assertFalse(budget.tryAcquireRetry());

        timeSource.advanceMillis(1);
        assertEquals(2, budget.remainingRetries());
        assertTrue(budget.tryAcquireRetry());
        assertEquals(1, budget.usedRetries());
    }

    @Test
    void remainingRetriesDoesNotConsumeBudget() {
        RetryBudget budget = new RetryBudget(3, 1_000, new ManualTimeSource(0));

        assertEquals(3, budget.remainingRetries());
        assertEquals(3, budget.remainingRetries());
        assertTrue(budget.tryAcquireRetry());
        assertEquals(2, budget.remainingRetries());
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new RetryBudget(0, 1_000, new ManualTimeSource(0)));
        assertThrows(IllegalArgumentException.class, () -> new RetryBudget(2, 0, new ManualTimeSource(0)));
        assertThrows(IllegalArgumentException.class, () -> new RetryBudget(2, 1_000, null));
    }
}
