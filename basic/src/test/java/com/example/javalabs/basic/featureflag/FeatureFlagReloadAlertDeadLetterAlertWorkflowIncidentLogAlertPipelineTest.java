package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.TimeSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipelineTest {

    @Test
    void dispatchesCriticalIncidentLogAlertToOnCall() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log = criticalIncidentLog();
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline pipeline =
                pipeline(sink, new ManualTimeSource(0));

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult result = pipeline.run(log);

        assertTrue(result.delivered());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, result.healthReport().status());
        assertEquals(FeatureFlagReloadAlertChannel.ON_CALL, result.route().channel());
        assertEquals(1, sink.size());
        FeatureFlagReloadAlertDelivery delivery = sink.findAll().get(0);
        assertEquals("feature flag reload dead-letter alert workflow incident log needs attention",
                delivery.message());
        assertTrue(delivery.details().contains("incident log retained: 2/2"));
    }

    @Test
    void suppressesDuplicateIncidentLogAlertDuringCooldown() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log = criticalIncidentLog();
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        ManualTimeSource timeSource = new ManualTimeSource(0);
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline pipeline =
                pipeline(sink, timeSource);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult first = pipeline.run(log);
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult second = pipeline.run(log);

        assertTrue(first.delivered());
        assertFalse(second.delivered());
        assertEquals(FeatureFlagReloadAlertChannel.NONE, second.route().channel());
        assertEquals("alert cooldown active", second.decision().reason());
        assertEquals(1, sink.size());
    }

    @Test
    void skipsDispatchForHealthyIncidentLog() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(5);
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline pipeline =
                pipeline(sink, new ManualTimeSource(0));

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult result = pipeline.run(log);

        assertFalse(result.alert().active());
        assertFalse(result.delivered());
        assertEquals(FeatureFlagReloadAlertChannel.NONE, result.route().channel());
        assertEquals(0, sink.size());
    }

    @Test
    void rejectsInvalidInputs() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor monitor = monitor();
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy policy =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(true);
        FeatureFlagReloadAlertSuppressor suppressor =
                new FeatureFlagReloadAlertSuppressor(1_000, new ManualTimeSource(0));
        FeatureFlagReloadAlertRouter router = new FeatureFlagReloadAlertRouter();
        FeatureFlagReloadAlertDispatcher dispatcher = new FeatureFlagReloadAlertDispatcher(sink);

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline(
                        null, policy, suppressor, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline(
                        monitor, null, suppressor, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline(
                        monitor, policy, null, router, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline(
                        monitor, policy, suppressor, null, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline(
                        monitor, policy, suppressor, router, null));
        assertThrows(IllegalArgumentException.class,
                () -> pipeline(sink, new ManualTimeSource(0)).run(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertResult(
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

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline pipeline(
            InMemoryFeatureFlagReloadAlertSink sink,
            ManualTimeSource timeSource) {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPipeline(
                monitor(),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogAlertPolicy(true),
                new FeatureFlagReloadAlertSuppressor(1_000, timeSource),
                new FeatureFlagReloadAlertRouter(),
                new FeatureFlagReloadAlertDispatcher(sink)
        );
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor monitor() {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogMonitor(0.5, 0.9, 1, 0);
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog criticalIncidentLog() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(2);
        log.record(alertResult("first", FeatureFlagReloadAlertChannel.NONE, false));
        log.record(alertResult("second", FeatureFlagReloadAlertChannel.NONE, false));
        log.record(alertResult("third", FeatureFlagReloadAlertChannel.NONE, false));
        return log;
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult alertResult(
            String warning,
            FeatureFlagReloadAlertChannel channel,
            boolean delivered) {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport report =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        List.of(warning),
                        1.0,
                        delivered ? 0.0 : 1.0,
                        delivered ? 1.0 : 0.0
                );
        FeatureFlagReloadAlert alert = new FeatureFlagReloadAlert(
                true,
                FeatureFlagReloadHealthStatus.CRITICAL,
                "feature flag reload dead-letter alert workflow needs attention",
                List.of(warning)
        );
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                alert,
                delivered,
                delivered ? "alert emitted" : "alert cooldown active",
                1_000
        );
        FeatureFlagReloadAlertRoute route = new FeatureFlagReloadAlertRoute(
                channel,
                decision,
                delivered ? "critical reload alert routed to on-call" : "alert suppressed: alert cooldown active"
        );
        FeatureFlagReloadAlertDispatchResult dispatchResult = delivered
                ? FeatureFlagReloadAlertDispatchResult.delivered(new FeatureFlagReloadAlertDelivery(
                        channel,
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        alert.message(),
                        alert.details()
                ))
                : FeatureFlagReloadAlertDispatchResult.skipped("alert suppressed");
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult(
                report,
                alert,
                decision,
                route,
                dispatchResult
        );
    }
}

