package com.example.javalabs.basic.featureflag;

/**
 * Dead-letter record for an alert delivery that exhausted retry attempts.
 *
 * @param delivery alert payload that could not be delivered
 * @param failedAttempt exhausted attempt number
 * @param failedAtMillis timestamp when the delivery was dead-lettered
 * @param reason reason copied from the retry plan
 */
public record FeatureFlagReloadAlertDeadLetter(
        FeatureFlagReloadAlertDelivery delivery,
        int failedAttempt,
        long failedAtMillis,
        String reason) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when delivery, attempt, timestamp, or reason is invalid
     */
    public FeatureFlagReloadAlertDeadLetter {
        if (delivery == null) {
            throw new IllegalArgumentException("delivery must not be null");
        }
        if (failedAttempt <= 0) {
            throw new IllegalArgumentException("failedAttempt must be positive");
        }
        if (failedAtMillis < 0) {
            throw new IllegalArgumentException("failedAtMillis must not be negative");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
