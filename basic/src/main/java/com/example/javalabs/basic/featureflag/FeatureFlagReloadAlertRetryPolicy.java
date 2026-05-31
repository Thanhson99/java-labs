package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.TimeSource;

/**
 * Plans bounded retries for failed alert deliveries.
 */
public final class FeatureFlagReloadAlertRetryPolicy {

    private final int maxAttempts;
    private final long initialDelayMillis;
    private final double backoffMultiplier;
    private final TimeSource timeSource;

    /**
     * Creates a bounded retry policy for failed alert deliveries.
     *
     * @param maxAttempts maximum delivery attempts before giving up
     * @param initialDelayMillis delay before the second attempt
     * @param backoffMultiplier multiplier applied after each failed attempt
     * @param timeSource clock used to calculate next-attempt timestamps
     * @throws IllegalArgumentException when limits, multiplier, or clock are invalid
     */
    public FeatureFlagReloadAlertRetryPolicy(
            int maxAttempts,
            long initialDelayMillis,
            double backoffMultiplier,
            TimeSource timeSource) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be positive");
        }
        if (initialDelayMillis <= 0) {
            throw new IllegalArgumentException("initialDelayMillis must be positive");
        }
        if (backoffMultiplier < 1.0) {
            throw new IllegalArgumentException("backoffMultiplier must be >= 1");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.maxAttempts = maxAttempts;
        this.initialDelayMillis = initialDelayMillis;
        this.backoffMultiplier = backoffMultiplier;
        this.timeSource = timeSource;
    }

    /**
     * Builds a retry plan after a failed delivery attempt.
     *
     * @param delivery failed delivery payload
     * @param failedAttempt attempt number that just failed
     * @return retry-later plan or give-up plan
     * @throws IllegalArgumentException when inputs are invalid
     */
    public FeatureFlagReloadAlertRetryPlan planFailure(FeatureFlagReloadAlertDelivery delivery, int failedAttempt) {
        if (delivery == null) {
            throw new IllegalArgumentException("delivery must not be null");
        }
        if (failedAttempt <= 0) {
            throw new IllegalArgumentException("failedAttempt must be positive");
        }
        if (failedAttempt >= maxAttempts) {
            // Attempts are exhausted, so the caller should dead-letter the payload.
            return new FeatureFlagReloadAlertRetryPlan(
                    FeatureFlagReloadAlertRetryDecision.GIVE_UP,
                    failedAttempt,
                    timeSource.currentTimeMillis(),
                    "max alert delivery attempts exhausted"
            );
        }

        int nextAttempt = failedAttempt + 1;
        long delay = delayForAttempt(nextAttempt);
        return new FeatureFlagReloadAlertRetryPlan(
                FeatureFlagReloadAlertRetryDecision.RETRY_LATER,
                nextAttempt,
                timeSource.currentTimeMillis() + delay,
                "retry alert delivery later"
        );
    }

    /**
     * Calculates exponential delay for a future attempt.
     *
     * @param attempt future attempt number
     * @return delay in milliseconds
     */
    private long delayForAttempt(int attempt) {
        double multiplier = Math.pow(backoffMultiplier, attempt - 2);
        return Math.round(initialDelayMillis * multiplier);
    }
}


