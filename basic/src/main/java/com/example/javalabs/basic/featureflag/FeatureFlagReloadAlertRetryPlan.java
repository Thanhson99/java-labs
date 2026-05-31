package com.example.javalabs.basic.featureflag;

/**
 * Retry plan for a failed alert delivery attempt.
 *
 * @param decision whether another retry should be scheduled
 * @param attempt next attempt number, or the exhausted attempt number when giving up
 * @param nextAttemptAtMillis timestamp for the next attempt
 * @param reason short reason for the decision
 */
public record FeatureFlagReloadAlertRetryPlan(
        FeatureFlagReloadAlertRetryDecision decision,
        int attempt,
        long nextAttemptAtMillis,
        String reason) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when decision, attempt, timestamp, or reason is invalid
     */
    public FeatureFlagReloadAlertRetryPlan {
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
        if (attempt <= 0) {
            throw new IllegalArgumentException("attempt must be positive");
        }
        if (nextAttemptAtMillis < 0) {
            throw new IllegalArgumentException("nextAttemptAtMillis must not be negative");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }

    /**
     * @return {@code true} when another attempt should be scheduled
     */
    public boolean retryLater() {
        return decision == FeatureFlagReloadAlertRetryDecision.RETRY_LATER;
    }

    /**
     * @return {@code true} when retries are exhausted and the payload should be dead-lettered
     */
    public boolean giveUp() {
        return decision == FeatureFlagReloadAlertRetryDecision.GIVE_UP;
    }
}
