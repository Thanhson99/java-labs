package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Result of applying a snapshot retention policy.
 *
 * @param keptVersions versions still retained
 * @param prunedVersions versions that should be deleted
 */
public record FeatureFlagSnapshotRetentionReport(List<Long> keptVersions, List<Long> prunedVersions) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when any list is {@code null}
     */
    public FeatureFlagSnapshotRetentionReport {
        if (keptVersions == null) {
            throw new IllegalArgumentException("keptVersions must not be null");
        }
        if (prunedVersions == null) {
            throw new IllegalArgumentException("prunedVersions must not be null");
        }
        keptVersions = List.copyOf(keptVersions);
        prunedVersions = List.copyOf(prunedVersions);
    }

    /**
     * @return {@code true} when at least one snapshot should be pruned
     */
    public boolean hasPrunedSnapshots() {
        return !prunedVersions.isEmpty();
    }
}
