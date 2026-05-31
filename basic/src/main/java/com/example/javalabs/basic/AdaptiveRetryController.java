package com.example.javalabs.basic;

/**
 * Combines retry budget and SLA budget before allowing another retry.
 *
 * <p>Retries should be reduced when a service is already burning error budget. This controller
 * keeps local retry volume bounded and stops retrying when the wider SLA budget is exhausted.</p>
 */
public final class AdaptiveRetryController {

    private final RetryBudget retryBudget;
    private final SlaBudgetTracker slaBudgetTracker;

    /**
     * Creates a controller that checks both retry volume and SLA health.
     *
     * @param retryBudget local retry budget that caps retry traffic
     * @param slaBudgetTracker rolling SLA tracker used to stop retries during high failure burn
     * @throws IllegalArgumentException when a dependency is {@code null}
     */
    public AdaptiveRetryController(RetryBudget retryBudget, SlaBudgetTracker slaBudgetTracker) {
        if (retryBudget == null) {
            throw new IllegalArgumentException("retryBudget must not be null");
        }
        if (slaBudgetTracker == null) {
            throw new IllegalArgumentException("slaBudgetTracker must not be null");
        }
        this.retryBudget = retryBudget;
        this.slaBudgetTracker = slaBudgetTracker;
    }

    /**
     * Attempts to reserve one retry.
     *
     * <p>This method has a side effect: when retry budget is available, one retry slot is consumed.
     * Use {@link #preview()} when a caller only needs to inspect the decision without consuming
     * budget.</p>
     *
     * @return retry decision with a human-readable reason
     */
    public RetryDecision tryAcquireRetry() {
        SlaBudgetSnapshot snapshot = slaBudgetTracker.snapshot();
        if (snapshot.budgetExhausted()) {
            return new RetryDecision(false, "sla budget exhausted");
        }
        if (!retryBudget.tryAcquireRetry()) {
            return new RetryDecision(false, "retry budget exhausted");
        }
        return new RetryDecision(true, "retry allowed");
    }

    /**
     * Checks whether a retry would be allowed without consuming retry budget.
     *
     * @return read-only retry decision based on the current SLA and retry-budget state
     */
    public RetryDecision preview() {
        SlaBudgetSnapshot snapshot = slaBudgetTracker.snapshot();
        if (snapshot.budgetExhausted()) {
            return new RetryDecision(false, "sla budget exhausted");
        }
        if (retryBudget.remainingRetries() == 0) {
            return new RetryDecision(false, "retry budget exhausted");
        }
        return new RetryDecision(true, "retry allowed");
    }
}
