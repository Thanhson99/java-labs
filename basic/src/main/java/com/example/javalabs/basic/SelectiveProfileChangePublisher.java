package com.example.javalabs.basic;

/**
 * Publishes a profile change event only when the before/after snapshots are different.
 */
public final class SelectiveProfileChangePublisher {

    private final UserProfileDiffService diffService;
    private final UserProfileChangePublisher publisher;

    /**
     * Creates a publisher that emits only meaningful profile changes.
     *
     * @param diffService service used to compare before/after snapshots
     * @param publisher downstream event publisher
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public SelectiveProfileChangePublisher(
            UserProfileDiffService diffService,
            UserProfileChangePublisher publisher) {
        if (diffService == null) {
            throw new IllegalArgumentException("diffService must not be null");
        }
        if (publisher == null) {
            throw new IllegalArgumentException("publisher must not be null");
        }
        this.diffService = diffService;
        this.publisher = publisher;
    }

    /**
     * Publishes a change event when the two snapshots differ.
     *
     * @param before existing stored profile
     * @param after requested new profile
     * @return {@code true} when an event was published
     * @throws IllegalArgumentException when the snapshots are invalid
     */
    public boolean publishIfChanged(UserProfile before, UserProfile after) {
        UserProfileDiff diff = diffService.diff(before, after);
        if (!diff.hasChanges()) {
            return false;
        }

        // Emit only the derived change payload, not the full profile snapshots.
        publisher.publish(new UserProfileChangeEvent(diff.userId(), diff.changes()));
        return true;
    }
}
