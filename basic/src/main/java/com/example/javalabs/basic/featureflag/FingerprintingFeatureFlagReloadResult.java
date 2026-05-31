package com.example.javalabs.basic.featureflag;

import java.util.Optional;

/**
 * Result of comparing feature flag fingerprints before reload.
 *
 * @param previousFingerprint fingerprint from the live registry before checking new config
 * @param nextFingerprint fingerprint from the proposed config
 * @param reloadResult safe reload result when the config changed
 */
public record FingerprintingFeatureFlagReloadResult(
        FeatureFlagConfigFingerprint previousFingerprint,
        FeatureFlagConfigFingerprint nextFingerprint,
        Optional<SafeFeatureFlagReloadResult> reloadResult) {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when fingerprints or reload result container are {@code null}
     */
    public FingerprintingFeatureFlagReloadResult {
        if (previousFingerprint == null) {
            throw new IllegalArgumentException("previousFingerprint must not be null");
        }
        if (nextFingerprint == null) {
            throw new IllegalArgumentException("nextFingerprint must not be null");
        }
        if (reloadResult == null) {
            throw new IllegalArgumentException("reloadResult must not be null");
        }
    }

    /**
     * Creates a result for unchanged config.
     *
     * @param previousFingerprint live registry fingerprint
     * @param nextFingerprint proposed config fingerprint
     * @return skipped result without reload output
     */
    public static FingerprintingFeatureFlagReloadResult skipped(
            FeatureFlagConfigFingerprint previousFingerprint,
            FeatureFlagConfigFingerprint nextFingerprint) {
        return new FingerprintingFeatureFlagReloadResult(
                previousFingerprint,
                nextFingerprint,
                Optional.empty()
        );
    }

    /**
     * Creates a result for changed config after safe reload was checked.
     *
     * @param previousFingerprint live registry fingerprint before reload
     * @param nextFingerprint proposed config fingerprint
     * @param reloadResult safe reload result
     * @return checked result with reload output
     * @throws IllegalArgumentException when {@code reloadResult} is {@code null}
     */
    public static FingerprintingFeatureFlagReloadResult checked(
            FeatureFlagConfigFingerprint previousFingerprint,
            FeatureFlagConfigFingerprint nextFingerprint,
            SafeFeatureFlagReloadResult reloadResult) {
        if (reloadResult == null) {
            throw new IllegalArgumentException("reloadResult must not be null");
        }
        return new FingerprintingFeatureFlagReloadResult(
                previousFingerprint,
                nextFingerprint,
                Optional.of(reloadResult)
        );
    }

    /**
     * @return {@code true} when reload work was skipped because config was unchanged
     */
    public boolean skipped() {
        return reloadResult.isEmpty();
    }

    /**
     * @return {@code true} when config changed and safe reload was evaluated
     */
    public boolean changed() {
        return reloadResult.isPresent();
    }
}
