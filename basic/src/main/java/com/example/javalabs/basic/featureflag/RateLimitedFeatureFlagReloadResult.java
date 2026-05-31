package com.example.javalabs.basic.featureflag;

import java.util.Optional;

/**
 * Result of applying a rate limit before feature flag reload work.
 *
 * @param allowed whether this reload attempt was allowed by the limiter
 * @param remainingTokens tokens left for the reload key after the decision
 * @param fingerprintResult reload result when the attempt was allowed
 */
public record RateLimitedFeatureFlagReloadResult(
        boolean allowed,
        double remainingTokens,
        Optional<FingerprintingFeatureFlagReloadResult> fingerprintResult) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when token count or result container is invalid
     */
    public RateLimitedFeatureFlagReloadResult {
        if (remainingTokens < 0) {
            throw new IllegalArgumentException("remainingTokens must not be negative");
        }
        if (fingerprintResult == null) {
            throw new IllegalArgumentException("fingerprintResult must not be null");
        }
    }

    /**
     * Creates a blocked result.
     *
     * @param remainingTokens available tokens after the failed decision
     * @return blocked result
     * @throws IllegalArgumentException when {@code remainingTokens} is negative
     */
    public static RateLimitedFeatureFlagReloadResult blocked(double remainingTokens) {
        return new RateLimitedFeatureFlagReloadResult(false, remainingTokens, Optional.empty());
    }

    /**
     * Creates an allowed result.
     *
     * @param remainingTokens available tokens after consuming one token
     * @param fingerprintResult downstream fingerprinting result
     * @return allowed result with fingerprinting output
     * @throws IllegalArgumentException when {@code remainingTokens} is negative
     * @throws IllegalArgumentException when {@code fingerprintResult} is {@code null}
     */
    public static RateLimitedFeatureFlagReloadResult allowed(
            double remainingTokens,
            FingerprintingFeatureFlagReloadResult fingerprintResult) {
        if (fingerprintResult == null) {
            throw new IllegalArgumentException("fingerprintResult must not be null");
        }
        return new RateLimitedFeatureFlagReloadResult(true, remainingTokens, Optional.of(fingerprintResult));
    }

    /**
     * @return {@code true} when the token bucket blocked reload work
     */
    public boolean blocked() {
        return !allowed;
    }
}
