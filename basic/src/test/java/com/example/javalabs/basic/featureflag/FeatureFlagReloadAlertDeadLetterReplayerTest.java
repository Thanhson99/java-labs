package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterReplayerTest {

    @Test
    void replaysDeadLettersUpToLimit() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(5);
        store.record(delivery("first", FeatureFlagReloadAlertChannel.DASHBOARD), giveUpPlan());
        store.record(delivery("second", FeatureFlagReloadAlertChannel.ON_CALL), giveUpPlan());
        store.record(delivery("third", FeatureFlagReloadAlertChannel.ON_CALL), giveUpPlan());
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterReplayer replayer = new FeatureFlagReloadAlertDeadLetterReplayer(
                store,
                new FeatureFlagReloadAlertDispatcher(sink)
        );

        FeatureFlagReloadAlertReplayResult result = replayer.replay(2);

        assertEquals(2, result.requestedLimit());
        assertEquals(2, result.attempted());
        assertEquals(2, result.delivered());
        assertEquals(0, result.skipped());
        assertTrue(result.allDelivered());
        assertEquals(2, result.dispatchResults().size());
        assertEquals(List.of("first", "second"), sink.findAll().stream()
                .map(delivery -> delivery.details().get(0))
                .toList());
        assertEquals(3, store.size());
    }

    @Test
    void returnsEmptyResultWhenNoDeadLettersExist() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(5);
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterReplayer replayer = new FeatureFlagReloadAlertDeadLetterReplayer(
                store,
                new FeatureFlagReloadAlertDispatcher(sink)
        );

        FeatureFlagReloadAlertReplayResult result = replayer.replay(10);

        assertEquals(10, result.requestedLimit());
        assertEquals(0, result.attempted());
        assertEquals(0, result.delivered());
        assertEquals(0, result.skipped());
        assertFalse(result.allDelivered());
        assertTrue(result.dispatchResults().isEmpty());
        assertTrue(sink.findAll().isEmpty());
    }

    @Test
    void ignoresRetryLaterRecordsBecauseTheyAreNotStored() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(5);
        FeatureFlagReloadAlertRetryPlan retryLater = new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.RETRY_LATER,
                1,
                1_250,
                "retry later"
        );
        assertFalse(store.record(delivery("retry later", FeatureFlagReloadAlertChannel.DASHBOARD), retryLater));
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDeadLetterReplayer replayer = new FeatureFlagReloadAlertDeadLetterReplayer(
                store,
                new FeatureFlagReloadAlertDispatcher(sink)
        );

        FeatureFlagReloadAlertReplayResult result = replayer.replay(5);

        assertEquals(0, result.attempted());
        assertTrue(sink.findAll().isEmpty());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(1);
        InMemoryFeatureFlagReloadAlertSink sink = new InMemoryFeatureFlagReloadAlertSink();
        FeatureFlagReloadAlertDispatcher dispatcher = new FeatureFlagReloadAlertDispatcher(sink);

        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterReplayer(null, dispatcher));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterReplayer(store, null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetterReplayer(store, dispatcher).replay(0));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertReplayResult(0, 0, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertReplayResult(1, -1, 0, 0, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertReplayResult(1, 1, 1, 1, List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertReplayResult(1, 0, 0, 0, null));
    }

    private static FeatureFlagReloadAlertDelivery delivery(
            String detail,
            FeatureFlagReloadAlertChannel channel) {
        return new FeatureFlagReloadAlertDelivery(
                channel,
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
