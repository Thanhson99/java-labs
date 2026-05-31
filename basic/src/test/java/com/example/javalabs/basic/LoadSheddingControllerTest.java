package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadSheddingControllerTest {

    @Test
    void acceptsRequestWhenSystemIsHealthyAndQueueIsBelowSoftLimit() {
        LoadSheddingController controller = new LoadSheddingController(healthyTracker(), 5, 10);

        LoadSheddingDecision decision = controller.decide(new IncomingRequest("r-1", JobPriority.LOW), 4);

        assertTrue(decision.accepted());
        assertEquals("request accepted", decision.reason());
    }

    @Test
    void shedsLowPriorityWorkAtSoftQueueLimit() {
        LoadSheddingController controller = new LoadSheddingController(healthyTracker(), 5, 10);

        LoadSheddingDecision decision = controller.decide(new IncomingRequest("r-1", JobPriority.LOW), 5);

        assertFalse(decision.accepted());
        assertEquals("soft queue limit reached for low priority", decision.reason());
    }

    @Test
    void keepsHighPriorityWorkUntilHardQueueLimit() {
        LoadSheddingController controller = new LoadSheddingController(healthyTracker(), 5, 10);

        assertTrue(controller.decide(new IncomingRequest("r-1", JobPriority.HIGH), 9).accepted());
        LoadSheddingDecision decision = controller.decide(new IncomingRequest("r-2", JobPriority.HIGH), 10);

        assertFalse(decision.accepted());
        assertEquals("hard queue limit reached", decision.reason());
    }

    @Test
    void shedsNonHighPriorityWorkWhenSlaBudgetIsExhausted() {
        LoadSheddingController controller = new LoadSheddingController(exhaustedTracker(), 5, 10);

        LoadSheddingDecision normalDecision =
                controller.decide(new IncomingRequest("r-normal", JobPriority.NORMAL), 1);
        LoadSheddingDecision highDecision =
                controller.decide(new IncomingRequest("r-high", JobPriority.HIGH), 1);

        assertFalse(normalDecision.accepted());
        assertEquals("sla budget exhausted", normalDecision.reason());
        assertTrue(highDecision.accepted());
    }

    @Test
    void rejectsInvalidInputs() {
        SlaBudgetTracker tracker = healthyTracker();

        assertThrows(IllegalArgumentException.class, () -> new LoadSheddingController(null, 1, 2));
        assertThrows(IllegalArgumentException.class, () -> new LoadSheddingController(tracker, -1, 2));
        assertThrows(IllegalArgumentException.class, () -> new LoadSheddingController(tracker, 3, 2));

        LoadSheddingController controller = new LoadSheddingController(tracker, 1, 2);
        assertThrows(IllegalArgumentException.class, () -> controller.decide(null, 0));
        assertThrows(IllegalArgumentException.class,
                () -> controller.decide(new IncomingRequest("r-1", JobPriority.LOW), -1));
    }

    private static SlaBudgetTracker healthyTracker() {
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.80, 1_000, new ManualTimeSource(0));
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        return tracker;
    }

    private static SlaBudgetTracker exhaustedTracker() {
        SlaBudgetTracker tracker = new SlaBudgetTracker(0.80, 1_000, new ManualTimeSource(0));
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.SUCCESS);
        tracker.record(ServiceCallOutcome.FAILURE);
        tracker.record(ServiceCallOutcome.FAILURE);
        return tracker;
    }
}
