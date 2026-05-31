package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Audit event emitted when feature flag configuration changes.
 *
 * @param timestampMillis event timestamp
 * @param addedFlags added flag names
 * @param updatedFlags updated flag names
 * @param removedFlags removed flag names
 */
public record FeatureFlagAuditEvent(
        long timestampMillis,
        List<String> addedFlags,
        List<String> updatedFlags,
        List<String> removedFlags) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when timestamp or flag lists are invalid
     */
    public FeatureFlagAuditEvent {
        if (timestampMillis < 0) {
            throw new IllegalArgumentException("timestampMillis must not be negative");
        }
        if (addedFlags == null) {
            throw new IllegalArgumentException("addedFlags must not be null");
        }
        if (updatedFlags == null) {
            throw new IllegalArgumentException("updatedFlags must not be null");
        }
        if (removedFlags == null) {
            throw new IllegalArgumentException("removedFlags must not be null");
        }
        addedFlags = List.copyOf(addedFlags);
        updatedFlags = List.copyOf(updatedFlags);
        removedFlags = List.copyOf(removedFlags);
    }

    /**
     * @return number of changed flags represented by this event
     */
    public int changeCount() {
        return addedFlags.size() + updatedFlags.size() + removedFlags.size();
    }
}
