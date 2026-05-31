package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagReloadAlertDeadLetterStoreTest {

    @Test
    void recordsDeliveryOnlyWhenRetryPlanGivesUp() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(2);
        FeatureFlagReloadAlertDelivery delivery = delivery("critical one");
        FeatureFlagReloadAlertRetryPlan retryLater = new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.RETRY_LATER,
                2,
                1_000,
                "retry later"
        );
        FeatureFlagReloadAlertRetryPlan giveUp = new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.GIVE_UP,
                3,
                2_000,
                "max alert delivery attempts exhausted"
        );

        assertFalse(store.record(delivery, retryLater));
        assertTrue(store.record(delivery, giveUp));

        assertEquals(1, store.size());
        FeatureFlagReloadAlertDeadLetter record = store.findAll().get(0);
        assertEquals(delivery, record.delivery());
        assertEquals(3, record.failedAttempt());
        assertEquals(2_000, record.failedAtMillis());
        assertEquals("max alert delivery attempts exhausted", record.reason());
    }

    @Test
    void dropsOldestRecordWhenCapacityIsExceeded() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(2);
        FeatureFlagReloadAlertRetryPlan giveUp = new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.GIVE_UP,
                3,
                2_000,
                "max alert delivery attempts exhausted"
        );

        store.record(delivery("first"), giveUp);
        store.record(delivery("second"), giveUp);
        store.record(delivery("third"), giveUp);

        List<FeatureFlagReloadAlertDeadLetter> records = store.findAll();
        assertEquals(2, records.size());
        assertEquals(1, store.droppedCount());
        assertEquals(List.of("second", "third"), records.stream()
                .map(record -> record.delivery().details().get(0))
                .toList());
    }

    @Test
    void returnedRecordsAreImmutable() {
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(1);
        FeatureFlagReloadAlertRetryPlan giveUp = new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.GIVE_UP,
                3,
                2_000,
                "max alert delivery attempts exhausted"
        );
        store.record(delivery("critical"), giveUp);

        List<FeatureFlagReloadAlertDeadLetter> records = store.findAll();

        assertThrows(UnsupportedOperationException.class, () -> records.add(records.get(0)));
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadAlertDelivery delivery = delivery("critical");
        FeatureFlagReloadAlertRetryPlan giveUp = new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.GIVE_UP,
                3,
                2_000,
                "max alert delivery attempts exhausted"
        );

        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagReloadAlertDeadLetterStore(0));
        FeatureFlagReloadAlertDeadLetterStore store = new FeatureFlagReloadAlertDeadLetterStore(1);
        assertThrows(IllegalArgumentException.class, () -> store.record(null, giveUp));
        assertThrows(IllegalArgumentException.class, () -> store.record(delivery, null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetter(null, 1, 0, "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetter(delivery, 0, 0, "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetter(delivery, 1, -1, "reason"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadAlertDeadLetter(delivery, 1, 0, ""));
    }

    private static FeatureFlagReloadAlertDelivery delivery(String detail) {
        return new FeatureFlagReloadAlertDelivery(
                FeatureFlagReloadAlertChannel.ON_CALL,
                FeatureFlagReloadHealthStatus.CRITICAL,
                "feature flag reload workflow needs attention",
                List.of(detail)
        );
    }
}
