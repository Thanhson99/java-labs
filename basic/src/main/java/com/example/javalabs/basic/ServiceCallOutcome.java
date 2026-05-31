package com.example.javalabs.basic;

/**
 * Result of one service call for SLA tracking.
 */
public enum ServiceCallOutcome {
    /**
     * Call completed successfully.
     */
    SUCCESS,

    /**
     * Call failed and should count against error budget.
     */
    FAILURE
}
