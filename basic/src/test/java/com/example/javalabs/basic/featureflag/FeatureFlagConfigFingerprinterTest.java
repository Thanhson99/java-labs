package com.example.javalabs.basic.featureflag;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagConfigFingerprinterTest {

    @Test
    void createsSameFingerprintForSameRulesInDifferentOrder() {
        FeatureFlagConfigFingerprinter fingerprinter = new FeatureFlagConfigFingerprinter();

        FeatureFlagConfigFingerprint first = fingerprinter.fingerprint(List.of(
                new FeatureFlagRule("checkout", true, 25),
                new FeatureFlagRule("recommendations", true, 10)
        ));
        FeatureFlagConfigFingerprint second = fingerprinter.fingerprint(List.of(
                new FeatureFlagRule("recommendations", true, 10),
                new FeatureFlagRule("checkout", true, 25)
        ));

        assertTrue(first.sameConfig(second));
        assertEquals("SHA-256", first.algorithm());
        assertEquals(64, first.digest().length());
        assertEquals(2, first.ruleCount());
    }

    @Test
    void createsDifferentFingerprintWhenRuleChanges() {
        FeatureFlagConfigFingerprinter fingerprinter = new FeatureFlagConfigFingerprinter();

        FeatureFlagConfigFingerprint before = fingerprinter.fingerprint(List.of(
                new FeatureFlagRule("checkout", true, 25)
        ));
        FeatureFlagConfigFingerprint after = fingerprinter.fingerprint(List.of(
                new FeatureFlagRule("checkout", true, 50)
        ));

        assertFalse(before.sameConfig(after));
    }

    @Test
    void fingerprintsRegistrySnapshots() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 25)
        ));
        FeatureFlagConfigFingerprinter fingerprinter = new FeatureFlagConfigFingerprinter();

        FeatureFlagConfigFingerprint fromRegistry = fingerprinter.fingerprint(registry);
        FeatureFlagConfigFingerprint fromList = fingerprinter.fingerprint(List.of(
                new FeatureFlagRule("checkout", true, 25)
        ));

        assertTrue(fromRegistry.sameConfig(fromList));
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagConfigFingerprinter fingerprinter = new FeatureFlagConfigFingerprinter();

        assertThrows(IllegalArgumentException.class, () -> fingerprinter.fingerprint((List<FeatureFlagRule>) null));
        assertThrows(IllegalArgumentException.class, () -> fingerprinter.fingerprint((FeatureFlagRegistry) null));
        assertThrows(IllegalArgumentException.class, () -> fingerprinter.fingerprint(Arrays.asList(
                new FeatureFlagRule("checkout", true, 25),
                null
        )));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagConfigFingerprint("", "abc", 1));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagConfigFingerprint("SHA-256", "", 1));
        assertThrows(IllegalArgumentException.class, () -> new FeatureFlagConfigFingerprint("SHA-256", "abc", -1));
    }
}
