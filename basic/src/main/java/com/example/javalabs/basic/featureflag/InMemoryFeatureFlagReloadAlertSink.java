package com.example.javalabs.basic.featureflag;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory alert sink for tests and learning demos.
 */
public final class InMemoryFeatureFlagReloadAlertSink implements FeatureFlagReloadAlertSink {

    private final List<FeatureFlagReloadAlertDelivery> deliveries = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void deliver(FeatureFlagReloadAlertDelivery delivery) {
        if (delivery == null) {
            throw new IllegalArgumentException("delivery must not be null");
        }
        deliveries.add(delivery);
    }

    /**
     * @return immutable snapshot of delivered alerts
     */
    public List<FeatureFlagReloadAlertDelivery> findAll() {
        return List.copyOf(deliveries);
    }

    /**
     * @return number of delivered alerts
     */
    public int size() {
        return deliveries.size();
    }
}
