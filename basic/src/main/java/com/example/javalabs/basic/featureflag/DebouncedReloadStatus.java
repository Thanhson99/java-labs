package com.example.javalabs.basic.featureflag;

/**
 * State of a debounced feature flag reload attempt.
 */
public enum DebouncedReloadStatus {
    /**
     * No config is pending.
     */
    IDLE,

    /**
     * Config is pending but the quiet period has not elapsed.
     */
    WAITING,

    /**
     * Config was flushed to the downstream reload workflow.
     */
    FLUSHED
}
