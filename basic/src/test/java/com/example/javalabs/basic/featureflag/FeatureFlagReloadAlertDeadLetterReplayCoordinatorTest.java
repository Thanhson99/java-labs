package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterReplayCoordinatorTest {

    @Test
    void removesOnlyDeliveredDeadLettersAfterReplay() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(5);
        store.record(delivery("first"), giveUpPlan());
        store.record(delivery("second"), giveUpPlan());
        store.record(delivery("third"), giveUpPlan());
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterReplayCoordinator coordinator =
                new FeatureFlagReloadAlertDeadLetterReplayCoordinator(
                        store,
                        new FeatureFlagReloadAlertDispatcher(sink)
                );

        FeatureFlagReloadAlertReplayCleanupResult result = coordinator.replayAndRemoveDelivered(2);

        assertEquals(2, result.requestedLimit());
        assertEquals(2, result.attempted());
        assertEquals(2, result.delivered());
        assertEquals(0, result.skipped());
        assertEquals(2, result.removed());
        assertEquals(1, result.remaining());
        assertEquals(List.of("first", "second"), sink.findAll().stream()
                .map(delivery -> delivery.details().get(0))
                .toList());
        assertEquals(List.of("third"), store.findAll().stream()
                .map(record -> record.delivery().details().get(0))
                .toList());
    }

    @Test
    void returnsEmptyCleanupResultWhenStoreIsEmpty() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(5);
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterReplayCoordinator coordinator =
                new FeatureFlagReloadAlertDeadLetterReplayCoordinator(
                        store,
                        new FeatureFlagReloadAlertDispatcher(sink)
                );

        FeatureFlagReloadAlertReplayCleanupResult result = coordinator.replayAndRemoveDelivered(10);

        assertEquals(0, result.attempted());
        assertEquals(0, result.delivered());
        assertEquals(0, result.removed());
        assertEquals(0, result.remaining());
        assertTrue(result.dispatchResults().isEmpty());
        assertTrue(sink.findAll().isEmpty());
    }

    @Test
    void storeRemoveRejectsNullAndReportsMissingRecord() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(1);
        FeatureFlagReloadAlertDeadLetter missing = new FeatureFlagReloadAlertDeadLetter(
                delivery("missing"),
                3,
                2_000,
                "max alert delivery attempts exhausted"
        );

        assertThrows(IllegalArgumentException.class, () -> store.remove(null));
        assertFalse(store.remove(missing));
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(1);
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDispatcher dispatcher = new FeatureFlagReloadAlertDispatcher(sink);

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterReplayCoordinator(null, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterReplayCoordinator(store, null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterReplayCoordinator(store, dispatcher)
                        .replayAndRemoveDelivered(0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertReplayCleanupResult(0, 0, 0, 0, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertReplayCleanupResult(1, 1, 1, 1, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertReplayCleanupResult(1, 1, 1, 0, 2, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertReplayCleanupResult(1, 0, 0, 0, 0, 0, null));
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
