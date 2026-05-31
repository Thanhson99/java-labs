package com.example.javalabs.basic.featureflag;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

/**
 * Evaluates feature flags using stable percentage rollout.
 *
 * <p>The same flag and user id always map to the same bucket, which keeps the user experience
 * stable while allowing rollout percentage to increase gradually.</p>
 */
public final class FeatureFlagEvaluator {

    /**
     * Evaluates a feature flag for one user.
     *
     * @param rule feature flag rule that controls enablement and rollout percentage
     * @param userId stable user identifier used for percentage bucketing
     * @return immutable evaluation containing enabled state, bucket, and explanation
     * @throws IllegalArgumentException when {@code rule} is {@code null} or {@code userId} is blank
     */
    public FeatureFlagEvaluation evaluate(FeatureFlagRule rule, String userId) {
        if (rule == null) {
            throw new IllegalArgumentException("rule must not be null");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }

        int bucket = bucket(rule.flagName(), userId);
        if (!rule.enabled()) {
            return new FeatureFlagEvaluation(false, bucket, "flag disabled");
        }
        if (rule.rolloutPercentage() == 0) {
            return new FeatureFlagEvaluation(false, bucket, "rollout is zero");
        }
        // Bucket comparison keeps rollout deterministic: the same user remains in or out of rollout.
        if (bucket < rule.rolloutPercentage()) {
            return new FeatureFlagEvaluation(true, bucket, "user inside rollout");
        }
        return new FeatureFlagEvaluation(false, bucket, "user outside rollout");
    }

    /**
     * Calculates a stable rollout bucket from 0 to 99.
     *
     * <p>The flag name is part of the hash input so one user can be inside rollout for one feature
     * and outside rollout for another feature.</p>
     *
     * @param flagName feature flag name
     * @param userId stable user identifier
     * @return bucket between 0 and 99
     * @throws IllegalArgumentException when {@code flagName} or {@code userId} is blank
     */
    public int bucket(String flagName, String userId) {
        if (flagName == null || flagName.isBlank()) {
            throw new IllegalArgumentException("flagName must not be blank");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId must not be blank");
        }
        // CRC32 is simple and deterministic, which is enough for this learning example.
        CRC32 crc32 = new CRC32();
        crc32.update((flagName + ":" + userId).getBytes(StandardCharsets.UTF_8));
        return (int) (crc32.getValue() % 100);
    }
}
