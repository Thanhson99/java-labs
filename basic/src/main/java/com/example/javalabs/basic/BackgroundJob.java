package com.example.javalabs.basic;

/**
 * Small background job model used by the priority queue example.
 *
 * @param id stable job id
 * @param payload human-readable work payload
 * @param priority scheduling priority
 */
public record BackgroundJob(String id, String payload, JobPriority priority) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when id, payload, or priority is invalid
     */
    public BackgroundJob {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload must not be blank");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority must not be null");
        }
    }
}
