package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.TimeSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipelineTest {

    @Test
    void dispatchesCriticalWorkflowHealthAlertToOnCall() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline pipeline =
                pipeline(sink, new ManualTimeSource(0));

        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult result =
                pipeline.run(criticalSnapshot());

        assertTrue(result.delivered());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, result.healthReport().status());
        assertEquals(FeatureFlagReloadAlertChannel.ON_CALL, result.route().channel());
        assertEquals(1, sink.size());
        FeatureFlagReloadAlertDelivery delivery = sink.findAll().get(0);
        assertEquals("feature flag reload dead-letter alert workflow needs attention", delivery.message());
        assertTrue(delivery.details().contains("workflow critical rate: 100%"));
    }

    @Test
    void suppressesDuplicateWorkflowHealthAlertDuringCooldown() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        ManualTimeSource timeSource = new ManualTimeSource(0);
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline pipeline =
                pipeline(sink, timeSource);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult first =
                pipeline.run(criticalSnapshot());
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult second =
                pipeline.run(criticalSnapshot());

        assertTrue(first.delivered());
        assertFalse(second.delivered());
        assertEquals(FeatureFlagReloadAlertChannel.NONE, second.route().channel());
        assertEquals("alert cooldown active", second.decision().reason());
        assertEquals(1, sink.size());
    }

    @Test
    void skipsDispatchForHealthyWorkflowHealth() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline pipeline =
                pipeline(sink, new ManualTimeSource(0));

        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult result =
                pipeline.run(new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                        0, 0, 0, 0, 0, 0, 0, 0));

        assertFalse(result.alert().active());
        assertFalse(result.delivered());
        assertEquals(FeatureFlagReloadAlertChannel.NONE, result.route().channel());
        assertEquals(0, sink.size());
    }

    @Test
    void rejectsInvalidInputs() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer = analyzer();
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(true);
        FeatureFlagReloadAlertSuppressor suppressor =
                new FeatureFlagReloadAlertSuppressor(1_000, new ManualTimeSource(0));
        FeatureFlagReloadAlertRouter router = new FeatureFlagReloadAlertRouter();
        FeatureFlagReloadAlertDispatcher dispatcher = new FeatureFlagReloadAlertDispatcher(sink);

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline(
                        null, policy, suppressor, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline(
                        analyzer, null, suppressor, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline(
                        analyzer, policy, null, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline(
                        analyzer, policy, suppressor, null, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline(
                        analyzer, policy, suppressor, router, null));
        assertThrows(IllegalArgumentException.class,
                () -> pipeline(sink, new ManualTimeSource(0)).run(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult(
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

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline pipeline(
            InMemoryFeatureFlagReloadAlertSink sink,
            ManualTimeSource timeSource) {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPipeline(
                analyzer(),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertPolicy(true),
                new FeatureFlagReloadAlertSuppressor(1_000, timeSource),
                new FeatureFlagReloadAlertRouter(),
                new FeatureFlagReloadAlertDispatcher(sink)
        );
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer analyzer() {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthAnalyzer(0.25, 0.75, 0.5, 0.8);
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot criticalSnapshot() {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowMetricsSnapshot(
                4,
                0,
                0,
                4,
                4,
                0,
                4,
                0
        );
    }
}

