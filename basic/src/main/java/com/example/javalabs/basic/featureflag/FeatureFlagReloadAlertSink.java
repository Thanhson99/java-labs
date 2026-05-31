package com.example.javalabs.basic.featureflag;

/**
 * Destination abstraction for delivered feature flag reload alerts.
 */
public interface FeatureFlagReloadAlertSink {

    /**
     * Delivers one alert payload.
     *
     * @param delivery alert delivery payload
     * @throws IllegalArgumentException when {@code delivery} is {@code null}
     */
    void deliver(FeatureFlagReloadAlertDelivery delivery);
}
