package com.example.javalabs.basic;

/**
 * Request metadata used by load-shedding decisions.
 *
 * @param requestId stable request id
 * @param priority importance of the request
 */
public record IncomingRequest(String requestId, JobPriority priority) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when request id or priority is invalid
     */
    public IncomingRequest {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId must not be blank");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority must not be null");
        }
    }
}
