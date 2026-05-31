package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * Replays stored reload alert dead letters through the normal dispatcher.
 */
public final class FeatureFlagReloadAlertDeadLetterReplayer {

    private final FeatureFlagReloadAlertDeadLetterStore store;
    private final FeatureFlagReloadAlertDispatcher dispatcher;

    /**
     * Creates a replay service.
     *
     * @param store dead-letter store to read
     * @param dispatcher normal alert dispatcher used for replay
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public FeatureFlagReloadAlertDeadLetterReplayer(
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
     * Replays retained dead letters without removing them.
     *
     * @param limit maximum records to replay in this pass
     * @return replay summary
     * @throws IllegalArgumentException when {@code limit} is not positive
     */
    public FeatureFlagReloadAlertReplayResult replay(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }

        List<FeatureFlagReloadAlertDispatchResult> results = new ArrayList<>();
        int delivered = 0;
        int skipped = 0;
        for (FeatureFlagReloadAlertDeadLetter deadLetter : store.findAll().stream().limit(limit).toList()) {
            // Reuse normal dispatcher behavior so replay follows the same delivery path as live alerts.
            FeatureFlagReloadAlertDispatchResult result = dispatcher.dispatch(routeFrom(deadLetter));
            results.add(result);
            if (result.delivered()) {
                delivered++;
            } else {
                skipped++;
            }
        }

        return new FeatureFlagReloadAlertReplayResult(limit, results.size(), delivered, skipped, results);
    }

    /**
     * Reconstructs a deliverable route from a dead-letter record.
     *
     * @param deadLetter dead-letter record to replay
     * @return route that can be sent through the normal dispatcher
     * @throws IllegalArgumentException when {@code deadLetter} is {@code null}
     */
    static FeatureFlagReloadAlertRoute routeFrom(FeatureFlagReloadAlertDeadLetter deadLetter) {
        if (deadLetter == null) {
            throw new IllegalArgumentException("deadLetter must not be null");
        }
        FeatureFlagReloadAlertDelivery delivery = deadLetter.delivery();
        FeatureFlagReloadAlert alert = new FeatureFlagReloadAlert(
                true,
                delivery.severity(),
                delivery.message(),
                delivery.details()
        );
        FeatureFlagReloadAlertDecision decision = new FeatureFlagReloadAlertDecision(
                alert,
                true,
                "dead-letter replay",
                deadLetter.failedAtMillis()
        );
        return new FeatureFlagReloadAlertRoute(
                delivery.channel(),
                decision,
                "dead-letter replay routed to " + delivery.channel()
        );
    }
}
