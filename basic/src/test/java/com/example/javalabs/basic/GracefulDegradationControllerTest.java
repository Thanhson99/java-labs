package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GracefulDegradationControllerTest {

    @Test
    void returnsFullModeWhenLoadSheddingAcceptsRequest() {
        GracefulDegradationController controller = healthyController();

        DegradationDecision decision =
                controller.decide(new IncomingRequest("r-1", JobPriority.LOW), 1, true);

        assertEquals(ResponseMode.FULL, decision.mode());
        assertEquals("full response allowed", decision.reason());
    }

    @Test
    void returnsDegradedModeForShedLowPriorityRequestWhenDegradationIsAllowed() {
        GracefulDegradationController controller = healthyController();

        DegradationDecision decision =
                controller.decide(new IncomingRequest("r-1", JobPriority.LOW), 5, true);

        assertEquals(ResponseMode.DEGRADED, decision.mode());
        assertEquals("degraded response allowed", decision.reason());
    }

    @Test
    void rejectsWhenRequestCannotDegrade() {
        GracefulDegradationController controller = healthyController();

        DegradationDecision decision =
                controller.decide(new IncomingRequest("r-1", JobPriority.LOW), 5, false);

        assertEquals(ResponseMode.REJECTED, decision.mode());
        assertEquals("soft queue limit reached for low priority", decision.reason());
    }

    @Test
    void rejectsHighPriorityAtHardLimitInsteadOfDegrading() {
        GracefulDegradationController controller = healthyController();

        DegradationDecision decision =
                controller.decide(new IncomingRequest("r-1", JobPriority.HIGH), 10, true);

        assertEquals(ResponseMode.REJECTED, decision.mode());
        assertEquals("hard queue limit reached", decision.reason());
    }

    @Test
    void degradesNonHighPriorityWhenSlaBudgetIsExhausted() {
        GracefulDegradationController controller = exhaustedController();

        DegradationDecision decision =
                controller.decide(new IncomingRequest("r-1", JobPriority.NORMAL), 1, true);

        assertEquals(ResponseMode.DEGRADED, decision.mode());
    }

    @Test
    void rejectsInvalidDependency() {
        assertThrows(IllegalArgumentException.class, () -> new GracefulDegradationController(null));
    }

    private static GracefulDegradationController healthyController() {
        return new GracefulDegradationController(new LoadSheddingController(healthyTracker(), 5, 10));
    }

    private static GracefulDegradationController exhaustedController() {
        return new GracefulDegradationController(new LoadSheddingController(exhaustedTracker(), 5, 10));
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
