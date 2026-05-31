package com.example.javalabs.basic;

/**
 * Snapshot of service health inside the current rolling window.
 *
 * @param totalCalls calls counted in the window
 * @param failedCalls failed calls counted in the window
 * @param allowedFailures maximum failures allowed by the target availability
 */
public record SlaBudgetSnapshot(int totalCalls, int failedCalls, int allowedFailures) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when counters are inconsistent or negative
     */
    public SlaBudgetSnapshot {
        if (totalCalls < 0) {
            throw new IllegalArgumentException("totalCalls must not be negative");
        }
        if (failedCalls < 0 || failedCalls > totalCalls) {
            throw new IllegalArgumentException("failedCalls must be between 0 and totalCalls");
        }
        if (allowedFailures < 0) {
            throw new IllegalArgumentException("allowedFailures must not be negative");
        }
    }

    /**
     * @return number of successful calls in the window
     */
    public int successfulCalls() {
        return totalCalls - failedCalls;
    }

    /**
     * @return success ratio, or {@code 1.0} when no calls have been recorded
     */
    public double availability() {
        if (totalCalls == 0) {
            return 1.0;
        }
        return (double) successfulCalls() / totalCalls;
    }

    /**
     * @return failure ratio, or {@code 0.0} when no calls have been recorded
     */
    public double errorRate() {
        if (totalCalls == 0) {
            return 0.0;
        }
        return (double) failedCalls / totalCalls;
    }

    /**
     * @return remaining failures before the SLA budget is exhausted
     */
    public int remainingFailureBudget() {
        return Math.max(0, allowedFailures - failedCalls);
    }

    /**
     * @return {@code true} when failures exceed the allowed budget
     */
    public boolean budgetExhausted() {
        return failedCalls > allowedFailures;
    }
}
