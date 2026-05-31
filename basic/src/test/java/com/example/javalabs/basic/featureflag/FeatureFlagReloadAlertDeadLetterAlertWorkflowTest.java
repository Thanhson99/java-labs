package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.TimeSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowTest {

    @Test
    void dispatchesCriticalDeadLetterAlertToOnCall() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(2);
        store.record(delivery("first"), giveUpPlan());
        store.record(delivery("second"), giveUpPlan());
        store.record(delivery("third"), giveUpPlan());
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflow workflow = workflow(sink, new ManualTimeSource(0));

        FeatureFlagReloadAlertDeadLetterAlertWorkflowResult result = workflow.run(store);

        assertTrue(result.delivered());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, result.healthReport().status());
        assertEquals(FeatureFlagReloadAlertChannel.ON_CALL, result.route().channel());
        assertEquals(1, sink.size());
        FeatureFlagReloadAlertDelivery delivered = sink.findAll().get(0);
        assertEquals(FeatureFlagReloadAlertChannel.ON_CALL, delivered.channel());
        assertEquals("feature flag reload alert dead-letter backlog needs attention", delivered.message());
        assertTrue(delivered.details().contains("dead-letter backlog: 2/2"));
    }

    @Test
    void suppressesDuplicateDeadLetterAlertDuringCooldown() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(1);
        store.record(delivery("first"), giveUpPlan());
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        ManualTimeSource timeSource = new ManualTimeSource(0);
        FeatureFlagReloadAlertDeadLetterAlertWorkflow workflow = workflow(sink, timeSource);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowResult first = workflow.run(store);
        FeatureFlagReloadAlertDeadLetterAlertWorkflowResult second = workflow.run(store);

        assertTrue(first.delivered());
        assertFalse(second.delivered());
        assertEquals(FeatureFlagReloadAlertChannel.NONE, second.route().channel());
        assertEquals("alert cooldown active", second.decision().reason());
        assertEquals(1, sink.size());
    }

    @Test
    void skipsDispatchWhenDeadLetterStoreIsHealthy() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(5);
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflow workflow = workflow(sink, new ManualTimeSource(0));

        FeatureFlagReloadAlertDeadLetterAlertWorkflowResult result = workflow.run(store);

        assertFalse(result.alert().active());
        assertFalse(result.delivered());
        assertEquals(FeatureFlagReloadAlertChannel.NONE, result.route().channel());
        assertEquals(0, sink.size());
    }

    @Test
    void rejectsInvalidInputs() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterMonitor monitor =
                new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, 0);
        FeatureFlagReloadAlertDeadLetterAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(true);
        FeatureFlagReloadAlertSuppressor suppressor =
                new FeatureFlagReloadAlertSuppressor(1_000, new ManualTimeSource(0));
        FeatureFlagReloadAlertRouter router = new FeatureFlagReloadAlertRouter();
        FeatureFlagReloadAlertDispatcher dispatcher = new FeatureFlagReloadAlertDispatcher(sink);

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflow(
                        null, policy, suppressor, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflow(
                        monitor, null, suppressor, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflow(
                        monitor, policy, null, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflow(
                        monitor, policy, suppressor, null, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflow(
                        monitor, policy, suppressor, router, null));
        assertThrows(IllegalArgumentException.class,
                () -> workflow(sink, new ManualTimeSource(0)).run(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowResult(
                        null,
                        FeatureFlagReloadAlert.inactive(),
                        new FeatureFlagReloadAlertDecision(
                                FeatureFlagReloadAlert.inactive(),
                                false,
                                "inactive alert",
                                0
                        ),
                        new FeatureFlagReloadAlertRoute(
                                FeatureFlagReloadAlertChannel.NONE,
                                new FeatureFlagReloadAlertDecision(
                                        FeatureFlagReloadAlert.inactive(),
                                        false,
                                        "inactive alert",
                                        0
                                ),
                                "alert suppressed: inactive alert"
                        ),
                        FeatureFlagReloadAlertDispatchResult.skipped("inactive alert")
                ));
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflow workflow(
            InMemoryFeatureFlagReloadAlertSink sink,
            ManualTimeSource timeSource) {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflow(
                new FeatureFlagReloadAlertDeadLetterMonitor(0.5, 0.9, 0),
                new FeatureFlagReloadAlertDeadLetterAlertPolicy(true),
                new FeatureFlagReloadAlertSuppressor(1_000, timeSource),
                new FeatureFlagReloadAlertRouter(),
                new FeatureFlagReloadAlertDispatcher(sink)
        );
    }

    private static FeatureFlagReloadAlertDelivery delivery(String detail) {
        return new FeatureFlagReloadAlertDelivery(
                FeatureFlagReloadAlertChannel.ON_CALL,
                FeatureFlagReloadHealthStatus.CRITICAL,
                "feature flag reload workflow needs attention",
                List.of(detail)
        );
    }

    private static FeatureFlagReloadAlertRetryPlan giveUpPlan() {
        return new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.GIVE_UP,
                3,
                2_000,
                "max alert delivery attempts exhausted"
        );
    }
}

