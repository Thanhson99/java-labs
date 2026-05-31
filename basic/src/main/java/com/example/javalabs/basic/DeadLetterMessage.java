package com.example.javalabs.basic;

/**
 * Failed side-effect message captured for later inspection or replay.
 *
 * @param key stable message key
 * @param payload human-readable payload
 * @param reason failure reason
 */
public record DeadLetterMessage(String key, String payload, String reason) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when key, payload, or reason is blank
     */
    public DeadLetterMessage {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload must not be blank");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
    }
}
