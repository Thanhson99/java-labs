package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.TokenBucketRateLimiter;
import java.util.List;

/**
 * Applies a token-bucket guard before feature flag reload work.
 *
 * <p>This prevents a noisy config source or admin UI from triggering reload work too frequently.</p>
 */
public final class RateLimitedFeatureFlagReloader {

    private final String reloadKey;
    private final TokenBucketRateLimiter rateLimiter;
    private final FingerprintingFeatureFlagReloader delegate;

    /**
     * Creates a rate-limited reload wrapper.
     *
     * @param reloadKey stable key used by the token bucket
     * @param rateLimiter token-bucket limiter
     * @param delegate fingerprinting reload workflow
     * @throws IllegalArgumentException when inputs are invalid
     */
    public RateLimitedFeatureFlagReloader(
            String reloadKey,
            TokenBucketRateLimiter rateLimiter,
            FingerprintingFeatureFlagReloader delegate) {
        if (reloadKey == null || reloadKey.isBlank()) {
            throw new IllegalArgumentException("reloadKey must not be blank");
        }
        if (rateLimiter == null) {
            throw new IllegalArgumentException("rateLimiter must not be null");
        }
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        this.reloadKey = reloadKey;
        this.rateLimiter = rateLimiter;
        this.delegate = delegate;
    }

    /**
     * Reloads only when the token bucket allows this attempt.
     *
     * @param newRules proposed complete rule set
     * @return blocked result or allowed fingerprinting result
     */
    public RateLimitedFeatureFlagReloadResult reloadIfAllowed(List<FeatureFlagRule> newRules) {
        if (!rateLimiter.allow(reloadKey)) {
            // Do not call the delegate when the limiter blocks; this protects downstream reload work.
            return RateLimitedFeatureFlagReloadResult.blocked(rateLimiter.availableTokens(reloadKey));
        }

        FingerprintingFeatureFlagReloadResult fingerprintResult = delegate.reloadIfChanged(newRules);
        return RateLimitedFeatureFlagReloadResult.allowed(
                rateLimiter.availableTokens(reloadKey),
                fingerprintResult
        );
    }
}

