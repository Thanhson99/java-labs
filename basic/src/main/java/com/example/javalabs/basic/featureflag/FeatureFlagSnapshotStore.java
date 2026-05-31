package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Keeps versioned feature flag snapshots for rollback.
 */
public final class FeatureFlagSnapshotStore {

    private final List<FeatureFlagSnapshot> snapshots = new ArrayList<>();

    /**
     * Saves a new snapshot version.
     *
     * @param snapshot snapshot to save
     * @throws IllegalArgumentException when snapshot is {@code null} or version already exists
     */
    public void save(FeatureFlagSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot must not be null");
        }
        if (find(snapshot.version()).isPresent()) {
            throw new IllegalArgumentException("snapshot version already exists: " + snapshot.version());
        }
        snapshots.add(snapshot);
        // Keep history sorted so latest, restore, and retention behavior are deterministic.
        snapshots.sort(Comparator.comparingLong(FeatureFlagSnapshot::version));
    }

    /**
     * @return newest snapshot when history is not empty
     */
    public Optional<FeatureFlagSnapshot> latest() {
        if (snapshots.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(snapshots.get(snapshots.size() - 1));
    }

    /**
     * Finds a snapshot by version.
     *
     * @param version snapshot version
     * @return snapshot when present
     * @throws IllegalArgumentException when {@code version} is not positive
     */
    public Optional<FeatureFlagSnapshot> find(long version) {
        if (version <= 0) {
            throw new IllegalArgumentException("version must be positive");
        }
        return snapshots.stream()
                .filter(snapshot -> snapshot.version() == version)
                .findFirst();
    }

    /**
     * Restores a registry from a stored snapshot.
     *
     * @param version snapshot version to restore
     * @return new registry initialized with snapshot rules
     * @throws IllegalArgumentException when {@code version} is not positive
     * @throws IllegalStateException when the snapshot does not exist
     */
    public FeatureFlagRegistry restore(long version) {
        FeatureFlagSnapshot snapshot = find(version)
                .orElseThrow(() -> new IllegalStateException("snapshot not found: " + version));
        return new FeatureFlagRegistry(List.copyOf(snapshot.rules().values()));
    }

    /**
     * @return immutable snapshot history sorted by version
     */
    public List<FeatureFlagSnapshot> history() {
        return List.copyOf(snapshots);
    }

    /**
     * @return number of stored snapshots
     */
    public int size() {
        return snapshots.size();
    }
}
