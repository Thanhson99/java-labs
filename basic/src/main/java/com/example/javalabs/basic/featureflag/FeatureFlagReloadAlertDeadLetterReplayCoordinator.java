package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Replays dead-lettered reload alerts and acknowledges records that were delivered.
 */
public final class FeatureFlagReloadAlertDeadLetterReplayCoordinator {

    private final FeatureFlagReloadAlertDeadLetterStore store;
    private final FeatureFlagReloadAlertDispatcher dispatcher;

    /**
     * Creates a coordinator that replays and acknowledges delivered dead letters.
     *
     * @param store dead-letter store to read and remove from
     * @param dispatcher normal alert dispatcher used for replay
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterReplayCoordinator(
            FeatureFlagReloadAlertDeadLetterStore store,
            FeatureFlagReloadAlertDispatcher dispatcher) {
        if (store == null) {
            throw new IllegalArgumentException("store must not be null");
        }
        if (dispatcher == null) {
            throw new IllegalArgumentException("dispatcher must not be null");
        }
        this.store = store;
        this.dispatcher = dispatcher;
    }

    /**
     * Replays a bounded batch and removes records that were delivered.
     *
     * @param limit maximum records to replay
     * @return replay and cleanup summary
     * @throws IllegalArgumentException when {@code limit} is not positive
     */
    public FeatureFlagReloadAlertReplayCleanupResult replayAndRemoveDelivered(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }

        List<FeatureFlagReloadAlertDeadLetter> batch = store.findAll().stream()
                .limit(limit)
                .toList();
        List<FeatureFlagReloadAlertDispatchResult> results = new ArrayList<>();
        int delivered = 0;
        int skipped = 0;
        int removed = 0;

        for (FeatureFlagReloadAlertDeadLetter deadLetter : batch) {
            // Removal happens only after a successful dispatch result acknowledges delivery.
            FeatureFlagReloadAlertDispatchResult result =
                    dispatcher.dispatch(FeatureFlagReloadAlertDeadLetterReplayer.routeFrom(deadLetter));
            results.add(result);
            if (result.delivered()) {
                delivered++;
                if (store.remove(deadLetter)) {
                    removed++;
                }
            } else {
                skipped++;
            }
        }

        return new FeatureFlagReloadAlertReplayCleanupResult(
                limit,
                results.size(),
                delivered,
                skipped,
                removed,
                store.size(),
                results
        );
    }
}
