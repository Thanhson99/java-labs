package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Summary of applying a new feature flag configuration snapshot.
 *
 * @param addedFlags flags that did not exist before
 * @param updatedFlags flags whose rule changed
 * @param removedFlags flags removed from the new config
 * @param unchangedFlags flags that stayed the same
 */
public record FeatureFlagReloadReport(
        List<String> addedFlags,
        List<String> updatedFlags,
        List<String> removedFlags,
        List<String> unchangedFlags) {

    /**
     * Validates and defensively copies the generated record constructor arguments.
     *
     * @throws IllegalArgumentException when any list is {@code null}
     */
    public FeatureFlagReloadReport {
        if (addedFlags == null) {
            throw new IllegalArgumentException("addedFlags must not be null");
        }
        if (updatedFlags == null) {
            throw new IllegalArgumentException("updatedFlags must not be null");
        }
        if (removedFlags == null) {
            throw new IllegalArgumentException("removedFlags must not be null");
        }
        if (unchangedFlags == null) {
            throw new IllegalArgumentException("unchangedFlags must not be null");
        }
        addedFlags = List.copyOf(addedFlags);
        updatedFlags = List.copyOf(updatedFlags);
        removedFlags = List.copyOf(removedFlags);
        unchangedFlags = List.copyOf(unchangedFlags);
    }

    /**
     * @return {@code true} when the reload changed live configuration
     */
    public boolean hasChanges() {
        return !(addedFlags.isEmpty() && updatedFlags.isEmpty() && removedFlags.isEmpty());
    }

    /**
     * @return number of added, updated, and removed flags
     */
    public int changeCount() {
        return addedFlags.size() + updatedFlags.size() + removedFlags.size();
    }
}
