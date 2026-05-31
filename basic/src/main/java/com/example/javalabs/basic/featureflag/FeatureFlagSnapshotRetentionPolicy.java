package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Calculates which feature flag snapshots should be retained.
 *
 * <p>Rollback history is useful, but keeping every snapshot forever can grow storage without
 * bound. This policy keeps the newest N versions and reports older versions for pruning.</p>
 */
public final class FeatureFlagSnapshotRetentionPolicy {

    private final int maxSnapshots;

    /**
     * Creates a retention policy that keeps the newest snapshot versions.
     *
     * @param maxSnapshots maximum number of snapshots to keep
     * @throws IllegalArgumentException when {@code maxSnapshots} is not positive
     */
    public FeatureFlagSnapshotRetentionPolicy(int maxSnapshots) {
        if (maxSnapshots <= 0) {
            throw new IllegalArgumentException("maxSnapshots must be positive");
        }
        this.maxSnapshots = maxSnapshots;
    }

    /**
     * Calculates kept and pruned snapshot versions.
     *
     * @param snapshots snapshot history to evaluate
     * @return retention report
     * @throws IllegalArgumentException when {@code snapshots} is {@code null} or contains {@code null}
     */
    public FeatureFlagSnapshotRetentionReport apply(List<FeatureFlagSnapshot> snapshots) {
        if (snapshots == null) {
            throw new IllegalArgumentException("snapshots must not be null");
        }
        if (snapshots.stream().anyMatch(snapshot -> snapshot == null)) {
            throw new IllegalArgumentException("snapshots must not contain null");
        }

        List<FeatureFlagSnapshot> sorted = new ArrayList<>(snapshots);
        sorted.sort(Comparator.comparingLong(FeatureFlagSnapshot::version));

        int pruneCount = Math.max(0, sorted.size() - maxSnapshots);
        // Older versions are pruned first; the newest maxSnapshots versions are kept.
        List<Long> pruned = sorted.subList(0, pruneCount).stream()
                .map(FeatureFlagSnapshot::version)
                .toList();
        List<Long> kept = sorted.subList(pruneCount, sorted.size()).stream()
                .map(FeatureFlagSnapshot::version)
                .toList();
        return new FeatureFlagSnapshotRetentionReport(kept, pruned);
    }
}
