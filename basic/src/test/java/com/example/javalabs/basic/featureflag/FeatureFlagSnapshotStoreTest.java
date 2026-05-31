package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagSnapshotStoreTest {

    @Test
    void savesSnapshotsAndReturnsLatestVersion() {
        FeatureFlagSnapshotStore store = new FeatureFlagSnapshotStore();

        store.save(snapshot(1, new FeatureFlagRule("checkout", true, 10)));
        store.save(snapshot(2, new FeatureFlagRule("checkout", true, 20)));

        assertEquals(2, store.size());
        assertEquals(2, store.latest().orElseThrow().version());
    }

    @Test
    void restoreBuildsRegistryFromOldSnapshot() {
        FeatureFlagSnapshotStore store = new FeatureFlagSnapshotStore();
        store.save(snapshot(1, new FeatureFlagRule("checkout", true, 10)));
        store.save(snapshot(2, new FeatureFlagRule("checkout", true, 90)));

        FeatureFlagRegistry restored = store.restore(1);

        assertEquals(new FeatureFlagRule("checkout", true, 10), restored.find("checkout").orElseThrow());
    }

    @Test
    void duplicateRulesUseLastRuleInSnapshot() {
        FeatureFlagSnapshot snapshot = FeatureFlagSnapshot.fromRules(1, List.of(
                new FeatureFlagRule("checkout", true, 10),
                new FeatureFlagRule("checkout", true, 30)
        ));

        assertEquals(new FeatureFlagRule("checkout", true, 30), snapshot.rules().get("checkout"));
        assertEquals(1, snapshot.size());
    }

    @Test
    void historyCannotMutateStore() {
        FeatureFlagSnapshotStore store = new FeatureFlagSnapshotStore();
        store.save(snapshot(1, new FeatureFlagRule("checkout", true, 10)));

        assertThrows(UnsupportedOperationException.class, () -> store.history().clear());
        assertEquals(1, store.size());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagSnapshotStore store = new FeatureFlagSnapshotStore();

        assertThrows(IllegalArgumentException.class, () -> FeatureFlagSnapshot.fromRules(0, List.of()));
        assertThrows(IllegalArgumentException.class, () -> FeatureFlagSnapshot.fromRules(1, null));
        assertThrows(IllegalArgumentException.class, () -> store.save(null));
        assertThrows(IllegalArgumentException.class, () -> store.find(0));

        store.save(snapshot(1, new FeatureFlagRule("checkout", true, 10)));
        assertThrows(IllegalArgumentException.class,
                () -> store.save(snapshot(1, new FeatureFlagRule("search", true, 10))));
        assertThrows(IllegalStateException.class, () -> store.restore(99));
        assertTrue(store.find(2).isEmpty());
    }

    private static FeatureFlagSnapshot snapshot(long version, FeatureFlagRule rule) {
        return FeatureFlagSnapshot.fromRules(version, List.of(rule));
    }
}
