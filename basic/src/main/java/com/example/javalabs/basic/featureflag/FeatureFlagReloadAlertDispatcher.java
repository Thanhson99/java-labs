package com.example.javalabs.basic.featureflag;

/**
 * Sends deliverable alert routes to a sink.
 */
public final class FeatureFlagReloadAlertDispatcher {

    private final FeatureFlagReloadAlertSink sink;

    /**
     * Creates a dispatcher backed by an alert sink.
     *
     * @param sink sink that receives deliverable alerts
     * @throws IllegalArgumentException when {@code sink} is {@code null}
     */
    public FeatureFlagReloadAlertDispatcher(FeatureFlagReloadAlertSink sink) {
        if (sink == null) {
            throw new IllegalArgumentException("sink must not be null");
        }
        this.sink = sink;
    }

    /**
     * Sends a routed alert when the route is deliverable.
     *
     * @param route route to dispatch
     * @return dispatch result describing delivered or skipped outcome
     * @throws IllegalArgumentException when {@code route} is {@code null}
     */
    public FeatureFlagReloadAlertDispatchResult dispatch(FeatureFlagReloadAlertRoute route) {
        if (route == null) {
            throw new IllegalArgumentException("route must not be null");
        }
        if (!route.deliverable()) {
            return FeatureFlagReloadAlertDispatchResult.skipped(route.summary());
        }

        FeatureFlagReloadAlert alert = route.decision().alert();
        // Convert route + alert into the immutable payload expected by sinks.
        FeatureFlagReloadAlertDelivery delivery = new FeatureFlagReloadAlertDelivery(
                route.channel(),
                alert.severity(),
                alert.message(),
                alert.details()
        );
        sink.deliver(delivery);
        return FeatureFlagReloadAlertDispatchResult.delivered(delivery);
    }
}
