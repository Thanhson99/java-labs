package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilderTest {

    @Test
    void buildsDigestFromIncidentLog() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(3);
        log.record(alertResult("delivery latency is high", FeatureFlagReloadAlertChannel.ON_CALL, true));
        log.record(alertResult("sink is unavailable", FeatureFlagReloadAlertChannel.NONE, false));
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder builder = builder(12_345);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest digest = builder.build(log);

        assertEquals(12_345, digest.generatedAtMillis());
        assertEquals(2, digest.summary().totalIncidents());
        assertEquals(1, digest.summary().undeliveredIncidents());
        assertTrue(digest.hasActions());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, digest.severity());
        assertTrue(digest.formattedPlan().contains("Incident triage plan"));
        assertTrue(digest.formattedPlan().contains("Investigate alert delivery"));
    }

    @Test
    void returnsNoActionDigestForEmptyLog() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(3);

        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest digest = builder(99).build(log);

        assertEquals(99, digest.generatedAtMillis());
        assertEquals(0, digest.summary().totalIncidents());
        assertFalse(digest.hasActions());
        assertEquals(FeatureFlagReloadHealthStatus.HEALTHY, digest.severity());
        assertEquals("No incident triage actions.", digest.formattedPlan());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary summary =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummary(
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0);
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan plan =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlan(List.of());

        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder(
                null,
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner(),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter(),
                new ManualTimeSource(1)
        ));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder(
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer(),
                null,
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter(),
                new ManualTimeSource(1)
        ));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder(
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer(),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner(),
                null,
                new ManualTimeSource(1)
        ));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder(
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer(),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner(),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter(),
                null
        ));
        assertThrows(IllegalArgumentException.class, () -> builder(1).build(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest(-1, summary, plan, "ok"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest(1, null, plan, "ok"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest(1, summary, null, "ok"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigest(1, summary, plan, " "));
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder builder(long nowMillis) {
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageDigestBuilder(
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogSummarizer(),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriagePlanner(),
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentTriageFormatter(),
                new ManualTimeSource(nowMillis)
        );
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult alertResult(
            String warning,
            FeatureFlagReloadAlertChannel channel,
            boolean delivered) {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport healthReport =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        List.of(warning),
                        1.0,
                        delivered ? 0.0 : 1.0,
                        delivered ? 1.0 : 0.0
                );
        FeatureFlagReloadAlert alert = FeatureFlagReloadAlert.active(FeatureFlagReloadHealthStatus.CRITICAL, List.of(warning));
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                alert,
                delivered,
                delivered ? "ready to emit" : "muted",
                1_000
        );
        FeatureFlagReloadAlertRoute route = new FeatureFlagReloadAlertRoute(channel, decision, "test-route");
        FeatureFlagReloadAlertDispatchResult dispatchResult =
                delivered
                        ? FeatureFlagReloadAlertDispatchResult.delivered(
                                new FeatureFlagReloadAlertDelivery(
                                        channel,
                                        FeatureFlagReloadHealthStatus.CRITICAL,
                                        alert.message(),
                                        alert.details()
                                ))
                        : FeatureFlagReloadAlertDispatchResult.skipped("muted");
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult(
                healthReport,
                alert,
                decision,
                route,
                dispatchResult
        );
    }
}

