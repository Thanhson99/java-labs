package com.example.javalabs.basic;

/**
 * How much work should be done for a request.
 */
public enum ResponseMode {
    /**
     * Return the complete response.
     */
    FULL,

    /**
     * Return a cheaper fallback response.
     */
    DEGRADED,

    /**
     * Reject the request.
     */
    REJECTED
}
