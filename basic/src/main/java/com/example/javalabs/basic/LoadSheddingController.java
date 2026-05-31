package com.example.javalabs.basic;

/**
 * Rejects less important work when queue pressure or SLA burn is too high.
 *
 * <p>Load shedding protects a service under pressure. High-priority work is preserved as long as
 * possible, while low-priority work is rejected earlier when queue depth or SLA burn indicates risk.</p>
 */
public final class LoadSheddingController {

    private final SlaBudgetTracker slaBudgetTracker;
    private final int softQueueLimit;
    private final int hardQueueLimit;

    /**
     * Creates a controller with soft and hard queue thresholds.
     *
     * @param slaBudgetTracker rolling SLA tracker used to detect budget exhaustion
     * @param softQueueLimit queue size where low-priority work starts being rejected
     * @param hardQueueLimit queue size where all work is rejected
     * @throws IllegalArgumentException when dependencies or limits are invalid
     */
    public LoadSheddingController(SlaBudgetTracker slaBudgetTracker, int softQueueLimit, int hardQueueLimit) {
        if (slaBudgetTracker == null) {
            throw new IllegalArgumentException("slaBudgetTracker must not be null");
        }
        if (softQueueLimit < 0) {
            throw new IllegalArgumentException("softQueueLimit must not be negative");
        }
        if (hardQueueLimit < softQueueLimit) {
            throw new IllegalArgumentException("hardQueueLimit must be greater than or equal to softQueueLimit");
        }
        this.slaBudgetTracker = slaBudgetTracker;
        this.softQueueLimit = softQueueLimit;
        this.hardQueueLimit = hardQueueLimit;
    }

    /**
     * Decides whether an incoming request should be accepted.
     *
     * @param request incoming request metadata
     * @param queuedWork current number of queued work items
     * @return decision with accepted flag and explanation
     * @throws IllegalArgumentException when {@code request} is {@code null} or {@code queuedWork} is negative
     */
    public LoadSheddingDecision decide(IncomingRequest request, int queuedWork) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (queuedWork < 0) {
            throw new IllegalArgumentException("queuedWork must not be negative");
        }

        SlaBudgetSnapshot snapshot = slaBudgetTracker.snapshot();
        if (snapshot.budgetExhausted() && request.priority() != JobPriority.HIGH) {
            return new LoadSheddingDecision(false, "sla budget exhausted");
        }
        // Hard limit wins over priority because the service is already at capacity.
        if (queuedWork >= hardQueueLimit) {
            return new LoadSheddingDecision(false, "hard queue limit reached");
        }
        if (queuedWork >= softQueueLimit && request.priority() == JobPriority.LOW) {
            return new LoadSheddingDecision(false, "soft queue limit reached for low priority");
        }
        return new LoadSheddingDecision(true, "request accepted");
    }
}
