package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLogTest {

    @Test
    void recordsOnlyActiveWorkflowAlerts() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(3);

        assertFalse(log.record(inactiveResult()));
        assertTrue(log.record(activeResult("critical one", FeatureFlagReloadAlertChannel.ON_CALL, true)));

        assertEquals(1, log.size());
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident incident = log.findAll().get(0);
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, incident.status());
        assertEquals(FeatureFlagReloadAlertChannel.ON_CALL, incident.channel());
        assertTrue(incident.delivered());
        assertEquals("feature flag reload dead-letter alert workflow needs attention", incident.message());
        assertEquals(List.of("critical one"), incident.warnings());
    }

    @Test
    void dropsOldestIncidentWhenCapacityIsExceeded() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(2);

        log.record(activeResult("first", FeatureFlagReloadAlertChannel.ON_CALL, true));
        log.record(activeResult("second", FeatureFlagReloadAlertChannel.ON_CALL, true));
        log.record(activeResult("third", FeatureFlagReloadAlertChannel.NONE, false));

        assertEquals(2, log.size());
        assertEquals(2, log.capacity());
        assertEquals(1, log.droppedCount());
        assertEquals(List.of("second", "third"), log.findAll().stream()
                .map(incident -> incident.warnings().get(0))
                .toList());
    }

    @Test
    void returnedIncidentsAreImmutable() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog log =
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(1);
        log.record(activeResult("critical one", FeatureFlagReloadAlertChannel.ON_CALL, true));

        List<FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident> incidents = log.findAll();

        assertThrows(UnsupportedOperationException.class, () -> incidents.add(incidents.get(0)));
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult activeResult =
                activeResult("critical one", FeatureFlagReloadAlertChannel.ON_CALL, true);

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(1).record(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident(
                        null,
                        FeatureFlagReloadAlertChannel.ON_CALL,
                        true,
                        "message",
                        List.of("warning")
                ));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident(
                        FeatureFlagReloadHealthStatus.HEALTHY,
                        FeatureFlagReloadAlertChannel.ON_CALL,
                        true,
                        "message",
                        List.of("warning")
                ));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident(
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        null,
                        true,
                        "message",
                        List.of("warning")
                ));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident(
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        FeatureFlagReloadAlertChannel.ON_CALL,
                        true,
                        "",
                        List.of("warning")
                ));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncident(
                        FeatureFlagReloadHealthStatus.CRITICAL,
                        FeatureFlagReloadAlertChannel.ON_CALL,
                        true,
                        "message",
                        null
                ));

        assertTrue(new FeatureFlagReloadAlertDeadLetterAlertWorkflowIncidentLog(1).record(activeResult));
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult inactiveResult() {
        FeatureFlagReloadAlert alert = FeatureFlagReloadAlert.inactive();
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                alert,
                false,
                "inactive alert",
                0
        );
        FeatureFlagReloadAlertRoute route = new FeatureFlagReloadAlertRoute(
                FeatureFlagReloadAlertChannel.NONE,
                decision,
                "alert suppressed: inactive alert"
        );
        return new FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult(
                new FeatureFlagReloadAlertDeadLetterAlertWorkflowHealthReport(
                        FeatureFlagReloadHealthStatus.HEALTHY,
                        List.of(),
                        0.0,
                        0.0,
                        0.0
                ),
                alert,
                decision,
                route,
                FeatureFlagReloadAlertDispatchResult.skipped("inactive alert")
        );
    }

    private static FeatureFlagReloadAlertDeadLetterAlertWorkflowAlertResult activeResult(
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
