package com.example.javalabs.basic.autoscaling;

/**
 * Action recommended by {@link AutoscalingPolicy}.
 */
public enum ScalingAction {
    /**
     * Add capacity because load, latency, or errors indicate pressure.
     */
    SCALE_OUT,

    /**
     * Remove capacity because sustained demand is comfortably low.
     */
    SCALE_IN,

    /**
     * Keep current capacity because metrics are inside the stable band.
     */
    HOLD
}
