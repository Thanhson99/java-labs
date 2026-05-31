package com.example.javalabs.basic;

/**
 * Snapshot of client-call metrics collected by a decorator.
 *
 * @param totalCalls all attempted calls
 * @param successfulCalls calls that completed without exception
 * @param failedCalls calls that threw an exception
 * @param totalDurationMillis accumulated duration across all calls
 */
public record ClientCallMetrics(
        int totalCalls,
        int successfulCalls,
        int failedCalls,
        long totalDurationMillis) {

    /**
     * Calculates average duration over all attempted calls.
     *
     * @return average duration in milliseconds, or {@code 0.0} when there were no calls
     */
    public double averageDurationMillis() {
        if (totalCalls == 0) {
            return 0.0;
        }
        return (double) totalDurationMillis / totalCalls;
    }
}
