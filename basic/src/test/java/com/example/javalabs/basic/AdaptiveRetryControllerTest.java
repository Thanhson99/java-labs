package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdaptiveRetryControllerTest {

    @Test
    void allowsRetryWhenBothBudgetsHaveCapacity() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        RetryBudget retryBudget = new RetryBudget(2, 1_000, timeSource);
        SlaBudgetTracker slaBudgetTracker = healthySlaTracker(timeSource);
        AdaptiveRetryController controller = new AdaptiveRetryController(retryBudget, slaBudgetTracker);

        RetryDecision decision = controller.tryAcquireRetry();

        assertTrue(decision.allowed());
        assertEquals("retry allowed", decision.reason());
        assertEquals(1, retryBudget.usedRetries());
    }

    @Test
    void blocksRetryWhenRetryBudgetIsExhausted() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        RetryBudget retryBudget = new RetryBudget(1, 1_000, timeSource);
        SlaBudgetTracker slaBudgetTracker = healthySlaTracker(timeSource);
        AdaptiveRetryController controller = new AdaptiveRetryController(retryBudget, slaBudgetTracker);

        assertTrue(controller.tryAcquireRetry().allowed());
        RetryDecision decision = controller.tryAcquireRetry();

        assertFalse(decision.allowed());
        assertEquals("retry budget exhausted", decision.reason());
    }

    @Test
    void blocksRetryWhenSlaBudgetIsExhaustedWithoutConsumingRetryBudget() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        RetryBudget retryBudget = new RetryBudget(2, 1_000, timeSource);
        SlaBudgetTracker slaBudgetTracker = new SlaBudgetTracker(0.80, 1_000, timeSource);
        slaBudgetTracker.record(ServiceCallOutcome.SUCCESS);
        slaBudgetTracker.record(ServiceCallOutcome.SUCCESS);
        slaBudgetTracker.record(ServiceCallOutcome.SUCCESS);
        slaBudgetTracker.record(ServiceCallOutcome.FAILURE);
        slaBudgetTracker.record(ServiceCallOutcome.FAILURE);
        AdaptiveRetryController controller = new AdaptiveRetryController(retryBudget, slaBudgetTracker);

        RetryDecision decision = controller.tryAcquireRetry();

        assertFalse(decision.allowed());
        assertEquals("sla budget exhausted", decision.reason());
        assertEquals(0, retryBudget.usedRetries());
    }

    @Test
    void previewDoesNotConsumeRetryBudget() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        RetryBudget retryBudget = new RetryBudget(1, 1_000, timeSource);
        AdaptiveRetryController controller =
                new AdaptiveRetryController(retryBudget, healthySlaTracker(timeSource));

        assertTrue(controller.preview().allowed());
        assertEquals(0, retryBudget.usedRetries());
        assertTrue(controller.tryAcquireRetry().allowed());
        assertFalse(controller.preview().allowed());
    }

    @Test
    void rejectsInvalidDependencies() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        RetryBudget retryBudget = new RetryBudget(1, 1_000, timeSource);
        SlaBudgetTracker slaBudgetTracker = healthySlaTracker(timeSource);

        assertThrows(IllegalArgumentException.class, () -> new AdaptiveRetryController(null, slaBudgetTracker));
        assertThrows(IllegalArgumentException.class, () -> new AdaptiveRetryController(retryBudget, null));
    }

    private static SlaBudgetTracker healthySlaTracker(ManualTimeSource timeSource) {
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.80, 1_000, timeSource);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        return tracker;
    }
}
