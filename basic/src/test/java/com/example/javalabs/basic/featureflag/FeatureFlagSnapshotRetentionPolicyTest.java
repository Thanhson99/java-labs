package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagSnapshotRetentionPolicyTest {

    @Test
    void keepsNewestSnapshotsAndPrunesOldestVersions() {
        FeatureFlagSnapshotRetentionPolicy policy = new FeatureFlagSnapshotRetentionPolicy(2);

        FeatureFlagSnapshotRetentionReport report = policy.apply(List.of(
                snapshot(1),
                snapshot(2),
                snapshot(3),
                snapshot(4)
        ));

        assertEquals(List.of(3L, 4L), report.keptVersions());
        assertEquals(List.of(1L, 2L), report.prunedVersions());
        assertTrue(report.hasPrunedSnapshots());
    }

    @Test
    void sortsSnapshotsBeforeApplyingRetention() {
        FeatureFlagSnapshotRetentionPolicy policy = new FeatureFlagSnapshotRetentionPolicy(2);

        FeatureFlagSnapshotRetentionReport report = policy.apply(List.of(
                snapshot(3),
                snapshot(1),
                snapshot(2)
        ));

        assertEquals(List.of(2L, 3L), report.keptVersions());
        assertEquals(List.of(1L), report.prunedVersions());
    }

    @Test
    void reportsNoPruningWhenHistoryIsWithinLimit() {
        FeatureFlagSnapshotRetentionPolicy policy = new FeatureFlagSnapshotRetentionPolicy(3);

        FeatureFlagSnapshotRetentionReport report = policy.apply(List.of(snapshot(1), snapshot(2)));

        assertEquals(List.of(1L, 2L), report.keptVersions());
        assertEquals(List.of(), report.prunedVersions());
        assertFalse(report.hasPrunedSnapshots());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagSnapshotRetentionPolicy(0));

        FeatureFlagSnapshotRetentionPolicy policy = new FeatureFlagSnapshotRetentionPolicy(1);
        assertThrows(IllegalArgumentException.class, () -> policy.apply(null));
        assertThrows(IllegalArgumentException.class, () -> policy.apply(Arrays.asList(snapshot(1), null)));
    }

    private static FeatureFlagSnapshot snapshot(long version) {
        return FeatureFlagSnapshot.fromRules(version, List.of(
                new FeatureFlagRule("flag-" + version, true, 10)
        ));
    }
}
