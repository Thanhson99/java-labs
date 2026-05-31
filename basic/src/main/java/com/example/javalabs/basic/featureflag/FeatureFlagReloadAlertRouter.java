package com.example.javalabs.basic.featureflag;

/**
 * Chooses an alert destination after duplicate suppression has run.
 */
public final class FeatureFlagReloadAlertRouter {

    /**
     * Routes an emitted alert to the right destination.
     *
     * @param decision suppression decision
     * @return route containing channel and routing summary
     * @throws IllegalArgumentException when {@code decision} is {@code null}
     */
    public FeatureFlagReloadAlertRoute route(FeatureFlagReloadAlertDecision decision) {
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
        if (decision.suppressed()) {
            return new FeatureFlagReloadAlertRoute(
                    FeatureFlagReloadAlertChannel.NONE,
                    decision,
                    "alert suppressed: " + decision.reason()
            );
        }

        FeatureFlagReloadAlert alert = decision.alert();
        if (alert.severity() == FeatureFlagReloadHealthStatus.CRITICAL) {
            // Critical reload workflow health should wake an on-call operator.
            return new FeatureFlagReloadAlertRoute(
                    FeatureFlagReloadAlertChannel.ON_CALL,
                    decision,
                    "critical reload alert routed to on-call"
            );
        }

        return new FeatureFlagReloadAlertRoute(
                FeatureFlagReloadAlertChannel.DASHBOARD,
                decision,
                "warning reload alert routed to dashboard"
        );
    }
}
