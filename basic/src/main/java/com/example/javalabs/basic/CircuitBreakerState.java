package com.example.javalabs.basic;

/**
 * States used by a small circuit breaker example.
 *
 * <p>The state names match the common resilience pattern used to protect callers from repeatedly
 * invoking an unhealthy downstream dependency.</p>
 */
public enum CircuitBreakerState {
    /**
     * Requests are allowed and failures are counted toward the opening threshold.
     */
    CLOSED,

    /**
     * Requests fail fast without calling the downstream dependency.
     */
    OPEN,

    /**
     * A limited trial request is allowed after cooldown to check whether recovery is possible.
     */
    HALF_OPEN
}
