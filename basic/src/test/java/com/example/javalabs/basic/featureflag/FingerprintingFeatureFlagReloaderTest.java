package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FingerprintingFeatureFlagReloaderTest {

    @Test
    void skipsReloadWhenFingerprintMatchesEvenIfInputOrderDiffers() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 25),
                new FeatureFlagRule("recommendations", true, 10)
        ));
        FingerprintingFeatureFlagReloader reloader = reloader(registry);

        FingerprintingFeatureFlagReloadResult result = reloader.reloadIfChanged(List.of(
                new FeatureFlagRule("recommendations", true, 10),
                new FeatureFlagRule("checkout", true, 25)
        ));

        assertTrue(result.skipped());
        assertFalse(result.changed());
        assertTrue(result.reloadResult().isEmpty());
        assertTrue(result.previousFingerprint().sameConfig(result.nextFingerprint()));
        assertEquals(2, registry.size());
    }

    @Test
    void appliesSafeReloadWhenFingerprintChangesAndValidationPasses() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        FingerprintingFeatureFlagReloader reloader = reloader(registry);

        FingerprintingFeatureFlagReloadResult result = reloader.reloadIfChanged(List.of(
                new FeatureFlagRule("checkout", true, 20),
                new FeatureFlagRule("recommendations", true, 5)
        ));

        assertTrue(result.changed());
        assertTrue(result.reloadResult().orElseThrow().applied());
        assertEquals(new FeatureFlagRule("checkout", true, 20), registry.find("checkout").orElseThrow());
        assertEquals(new FeatureFlagRule("recommendations", true, 5), registry.find("recommendations").orElseThrow());
    }

    @Test
    void returnsRejectedSafeReloadWhenChangedConfigFailsValidation() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        FingerprintingFeatureFlagReloader reloader = reloader(registry);

        FingerprintingFeatureFlagReloadResult result = reloader.reloadIfChanged(List.of(
                new FeatureFlagRule("checkout", true, 90)
        ));

        assertTrue(result.changed());
        assertTrue(result.reloadResult().orElseThrow().rejected());
        assertEquals(new FeatureFlagRule("checkout", true, 10), registry.find("checkout").orElseThrow());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        FeatureFlagConfigFingerprinter fingerprinter = new FeatureFlagConfigFingerprinter();
        SafeFeatureFlagReloader safeReloader = new SafeFeatureFlagReloader(
                registry,
                new FeatureFlagReloadValidator(10, 10)
        );
        FeatureFlagConfigFingerprint fingerprint = fingerprinter.fingerprint(registry);
        SafeFeatureFlagReloadResult reloadResult =
                SafeFeatureFlagReloadResult.rejected(new FeatureFlagReloadValidationReport(List.of("blocked")));

        assertThrows(IllegalArgumentException.class,
                () -> new FingerprintingFeatureFlagReloader(null, fingerprinter, safeReloader));
        assertThrows(IllegalArgumentException.class,
                () -> new FingerprintingFeatureFlagReloader(registry, null, safeReloader));
        assertThrows(IllegalArgumentException.class,
                () -> new FingerprintingFeatureFlagReloader(registry, fingerprinter, null));
        assertThrows(IllegalArgumentException.class,
                () -> new FingerprintingFeatureFlagReloadResult(null, fingerprint, Optional.empty()));
        assertThrows(IllegalArgumentException.class,
                () -> new FingerprintingFeatureFlagReloadResult(fingerprint, null, Optional.empty()));
        assertThrows(IllegalArgumentException.class,
                () -> new FingerprintingFeatureFlagReloadResult(fingerprint, fingerprint, null));
        assertThrows(IllegalArgumentException.class,
                () -> FingerprintingFeatureFlagReloadResult.checked(fingerprint, fingerprint, null));

        FingerprintingFeatureFlagReloadResult checked =
                FingerprintingFeatureFlagReloadResult.checked(fingerprint, fingerprint, reloadResult);
        assertTrue(checked.changed());
    }

    private static FingerprintingFeatureFlagReloader reloader(FeatureFlagRegistry registry) {
        FeatureFlagReloadValidator validator = new FeatureFlagReloadValidator(25, 10);
        return new FingerprintingFeatureFlagReloader(
                registry,
                new FeatureFlagConfigFingerprinter(),
                new SafeFeatureFlagReloader(registry, validator)
        );
    }
}
