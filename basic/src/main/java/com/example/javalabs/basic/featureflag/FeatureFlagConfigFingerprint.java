package com.example.javalabs.basic.featureflag;

/**
 * Stable identity for a feature flag config snapshot.
 *
 * @param algorithm hashing algorithm used to create the digest
 * @param digest hex-encoded digest
 * @param ruleCount number of rules included in the fingerprint
 */
public record FeatureFlagConfigFingerprint(String algorithm, String digest, int ruleCount) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when algorithm, digest, or rule count is invalid
     */
    public FeatureFlagConfigFingerprint {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm must not be blank");
        }
        if (digest == null || digest.isBlank()) {
            throw new IllegalArgumentException("digest must not be blank");
        }
        if (ruleCount < 0) {
            throw new IllegalArgumentException("ruleCount must not be negative");
        }
    }

    /**
     * Compares two fingerprints by algorithm and digest.
     *
     * @param other fingerprint to compare
     * @return {@code true} when both fingerprints represent the same config identity
     */
    public boolean sameConfig(FeatureFlagConfigFingerprint other) {
        return other != null
                && algorithm.equals(other.algorithm())
                && digest.equals(other.digest());
    }
}
