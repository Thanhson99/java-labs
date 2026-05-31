package com.example.javalabs.basic;

/**
 * Chooses full, degraded, or rejected response mode based on load-shedding pressure.
 *
 * <p>Graceful degradation is different from pure rejection. When a request cannot receive the full
 * response but the workflow supports a cheaper fallback, the controller returns degraded mode.</p>
 */
public final class GracefulDegradationController {

    private final LoadSheddingController loadSheddingController;

    /**
     * Creates a degradation controller backed by load-shedding decisions.
     *
     * @param loadSheddingController controller that decides whether full work is allowed
     * @throws IllegalArgumentException when {@code loadSheddingController} is {@code null}
     */
    public GracefulDegradationController(LoadSheddingController loadSheddingController) {
        if (loadSheddingController == null) {
            throw new IllegalArgumentException("loadSheddingController must not be null");
        }
        this.loadSheddingController = loadSheddingController;
    }

    /**
     * Chooses the best response mode for the current pressure.
     *
     * @param request incoming request metadata
     * @param queuedWork current number of queued work items
     * @param canDegrade whether the caller has a cheaper fallback response available
     * @return full, degraded, or rejected response decision
     */
    public DegradationDecision decide(IncomingRequest request, int queuedWork, boolean canDegrade) {
        LoadSheddingDecision sheddingDecision = loadSheddingController.decide(request, queuedWork);
        if (sheddingDecision.accepted()) {
            return new DegradationDecision(ResponseMode.FULL, "full response allowed");
        }
        // High-priority work should not silently receive a degraded response in this example.
        if (canDegrade && request.priority() != JobPriority.HIGH) {
            return new DegradationDecision(ResponseMode.DEGRADED, "degraded response allowed");
        }
        return new DegradationDecision(ResponseMode.REJECTED, sheddingDecision.reason());
    }
}
