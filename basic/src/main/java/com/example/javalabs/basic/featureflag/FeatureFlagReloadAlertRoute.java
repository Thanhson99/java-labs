package com.example.javalabs.basic.featureflag;

/**
 * Routing decision for a feature flag reload alert.
 *
 * @param channel destination channel
 * @param decision suppression decision used as input
 * @param summary concise routing summary
 */
public record FeatureFlagReloadAlertRoute(
        FeatureFlagReloadAlertChannel channel,
        FeatureFlagReloadAlertDecision decision,
        String summary) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when channel, decision, or summary is invalid
     */
    public FeatureFlagReloadAlertRoute {
        if (channel == null) {
            throw new IllegalArgumentException("channel must not be null");
        }
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("summary must not be blank");
        }
    }

    /**
     * @return {@code true} when the route can be sent to a sink
     */
    public boolean deliverable() {
        return channel != FeatureFlagReloadAlertChannel.NONE;
    }
}
