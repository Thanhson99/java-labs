package com.example.javalabs.basic.featureflag;

import java.util.List;

/**
 * Skips safe reload work when a feature flag config fingerprint has not changed.
 */
public final class FingerprintingFeatureFlagReloader {

    private final FeatureFlagRegistry registry;
    private final FeatureFlagConfigFingerprinter fingerprinter;
    private final SafeFeatureFlagReloader safeReloader;

    /**
     * Creates a reloader that skips unchanged config snapshots.
     *
     * @param registry live registry used to compute the previous fingerprint
     * @param fingerprinter deterministic config fingerprinter
     * @param safeReloader reload workflow to run when config changed
     * @throws IllegalArgumentException when any dependency is {@code null}
     */
    public FingerprintingFeatureFlagReloader(
            FeatureFlagRegistry registry,
            FeatureFlagConfigFingerprinter fingerprinter,
            SafeFeatureFlagReloader safeReloader) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        if (fingerprinter == null) {
            throw new IllegalArgumentException("fingerprinter must not be null");
        }
        if (safeReloader == null) {
            throw new IllegalArgumentException("safeReloader must not be null");
        }
        this.registry = registry;
        this.fingerprinter = fingerprinter;
        this.safeReloader = safeReloader;
    }

    /**
     * Reloads only when the proposed config fingerprint differs from the live registry fingerprint.
     *
     * @param newRules proposed complete rule set
     * @return fingerprint comparison result and optional reload result
     */
    public FingerprintingFeatureFlagReloadResult reloadIfChanged(List<FeatureFlagRule> newRules) {
        FeatureFlagConfigFingerprint previousFingerprint = fingerprinter.fingerprint(registry);
        FeatureFlagConfigFingerprint nextFingerprint = fingerprinter.fingerprint(newRules);
        if (previousFingerprint.sameConfig(nextFingerprint)) {
            // Skip validation/mutation work when the canonical config identity is unchanged.
            return FingerprintingFeatureFlagReloadResult.skipped(previousFingerprint, nextFingerprint);
        }

        SafeFeatureFlagReloadResult reloadResult = safeReloader.reload(newRules);
        return FingerprintingFeatureFlagReloadResult.checked(
                previousFingerprint,
                nextFingerprint,
                reloadResult
        );
    }
}
