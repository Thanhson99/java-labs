package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDispatcherTest {

    @Test
    void skipsRoutesThatAreNotDeliverable() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDispatcher dispatcher = new FeatureFlagReloadAlertDispatcher(sink);
        FeatureFlagReloadAlertRoute route = new FeatureFlagReloadAlertRoute(
                FeatureFlagReloadAlertChannel.NONE,
                new FeatureFlagReloadAlertDecision(FeatureFlagReloadAlert.inactive(), false, "inactive alert", 0),
                "alert suppressed: inactive alert"
        );

        FeatureFlagReloadAlertDispatchResult result = dispatcher.dispatch(route);

        assertFalse(result.delivered());
        assertEquals("alert suppressed: inactive alert", result.reason());
        assertTrue(result.delivery().isEmpty());
        assertEquals(0, sink.size());
    }

    @Test
    void deliversDashboardRoutesToSink() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDispatcher dispatcher = new FeatureFlagReloadAlertDispatcher(sink);
        FeatureFlagReloadAlert alert = FeatureFlagReloadAlert.active(
                FeatureFlagReloadHealthStatus.WARNING,
                List.of("reload block rate is elevated: 30%")
        );
        FeatureFlagReloadAlertRoute route = new FeatureFlagReloadAlertRoute(
                FeatureFlagReloadAlertChannel.DASHBOARD,
                new FeatureFlagReloadAlertDecision(alert, true, "alert emitted", 1_000),
                "warning reload alert routed to dashboard"
        );

        FeatureFlagReloadAlertDispatchResult result = dispatcher.dispatch(route);

        assertTrue(result.delivered());
        assertEquals("alert delivered", result.reason());
        assertEquals(1, sink.size());
        assertEquals(FeatureFlagReloadAlertChannel.DASHBOARD, sink.findAll().get(0).channel());
        assertEquals(List.of("reload block rate is elevated: 30%"), result.delivery().orElseThrow().details());
    }

    @Test
    void deliversOnCallRoutesToSink() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDispatcher dispatcher = new FeatureFlagReloadAlertDispatcher(sink);
        FeatureFlagReloadAlert alert = FeatureFlagReloadAlert.active(
                FeatureFlagReloadHealthStatus.CRITICAL,
                List.of("reload rejection rate is critical: 60%")
        );
        FeatureFlagReloadAlertRoute route = new FeatureFlagReloadAlertRoute(
                FeatureFlagReloadAlertChannel.ON_CALL,
                new FeatureFlagReloadAlertDecision(alert, true, "alert emitted", 1_000),
                "critical reload alert routed to on-call"
        );

        FeatureFlagReloadAlertDispatchResult result = dispatcher.dispatch(route);

        assertTrue(result.delivered());
        assertEquals(FeatureFlagReloadAlertChannel.ON_CALL, sink.findAll().get(0).channel());
        assertEquals(FeatureFlagReloadHealthStatus.CRITICAL, result.delivery().orElseThrow().severity());
    }

    @Test
    void rejectsInvalidInputs() {
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDelivery delivery = new FeatureFlagReloadAlertDelivery(
                FeatureFlagReloadAlertChannel.DASHBOARD,
                FeatureFlagReloadHealthStatus.WARNING,
                "message",
                List.of("detail")
        );

        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertDispatcher(null));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertDispatcher(sink).dispatch(null));
        assertThrows(IllegalArgumentException.class, () -> sink.deliver(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDelivery(null, FeatureFlagReloadHealthStatus.WARNING, "message", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDelivery(FeatureFlagReloadAlertChannel.NONE, FeatureFlagReloadHealthStatus.WARNING, "message", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDelivery(FeatureFlagReloadAlertChannel.DASHBOARD, null, "message", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDelivery(FeatureFlagReloadAlertChannel.DASHBOARD, FeatureFlagReloadHealthStatus.WARNING, "", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDelivery(FeatureFlagReloadAlertChannel.DASHBOARD, FeatureFlagReloadHealthStatus.WARNING, "message", null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDispatchResult(false, "", Optional.empty()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDispatchResult(false, "reason", null));
        assertThrows(IllegalArgumentException.class,
                () -> FeatureFlagReloadAlertDispatchResult.delivered(null));

        FeatureFlagReloadAlertDispatchResult delivered = FeatureFlagReloadAlertDispatchResult.delivered(delivery);
        assertTrue(delivered.delivered());
    }
}
