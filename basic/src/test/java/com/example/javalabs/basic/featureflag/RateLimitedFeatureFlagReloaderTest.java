package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.TokenBucketRateLimiter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitedFeatureFlagReloaderTest {

    @Test
    void allowsReloadUntilTokensAreConsumedThenBlocks() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        RateLimitedFeatureFlagReloader reloader = reloader(registry, new ManualTimeSource(0), 1, 1.0);

        RateLimitedFeatureFlagReloadResult first = reloader.reloadIfAllowed(List.of(
                new FeatureFlagRule("checkout", true, 20)
        ));
        RateLimitedFeatureFlagReloadResult second = reloader.reloadIfAllowed(List.of(
                new FeatureFlagRule("checkout", true, 25)
        ));

        assertTrue(first.allowed());
        assertFalse(first.blocked());
        assertTrue(first.fingerprintResult().orElseThrow().changed());
        assertEquals(new FeatureFlagRule("checkout", true, 20), registry.find("checkout").orElseThrow());
        assertTrue(second.blocked());
        assertTrue(second.fingerprintResult().isEmpty());
        assertEquals(new FeatureFlagRule("checkout", true, 20), registry.find("checkout").orElseThrow());
    }

    @Test
    void allowsReloadAgainAfterBucketRefills() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        RateLimitedFeatureFlagReloader reloader = reloader(registry, clock, 1, 1.0);

        assertTrue(reloader.reloadIfAllowed(List.of(new FeatureFlagRule("checkout", true, 20))).allowed());
        assertTrue(reloader.reloadIfAllowed(List.of(new FeatureFlagRule("checkout", true, 25))).blocked());

        clock.advanceMillis(1_000);

        RateLimitedFeatureFlagReloadResult third = reloader.reloadIfAllowed(List.of(
                new FeatureFlagRule("checkout", true, 25)
        ));

        assertTrue(third.allowed());
        assertEquals(new FeatureFlagRule("checkout", true, 25), registry.find("checkout").orElseThrow());
    }

    @Test
    void stillConsumesTokenWhenFingerprintSkipsUnchangedConfig() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        RateLimitedFeatureFlagReloader reloader = reloader(registry, new ManualTimeSource(0), 1, 1.0);

        RateLimitedFeatureFlagReloadResult first = reloader.reloadIfAllowed(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        RateLimitedFeatureFlagReloadResult second = reloader.reloadIfAllowed(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));

        assertTrue(first.allowed());
        assertTrue(first.fingerprintResult().orElseThrow().skipped());
        assertTrue(second.blocked());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(1, 1.0, new ManualTimeSource(0));
        FingerprintingFeatureFlagReloader delegate = fingerprintingReloader(registry);
        FingerprintingFeatureFlagReloadResult fingerprintResult =
                delegate.reloadIfChanged(List.of());

        assertThrows(IllegalArgumentException.class, () -> new RateLimitedFeatureFlagReloader("", limiter, delegate));
        assertThrows(IllegalArgumentException.class, () -> new RateLimitedFeatureFlagReloader("flags", null, delegate));
        assertThrows(IllegalArgumentException.class, () -> new RateLimitedFeatureFlagReloader("flags", limiter, null));
        assertThrows(IllegalArgumentException.class,
                () -> new RateLimitedFeatureFlagReloadResult(true, -0.1, Optional.empty()));
        assertThrows(IllegalArgumentException.class,
                () -> new RateLimitedFeatureFlagReloadResult(true, 0.0, null));
        assertThrows(IllegalArgumentException.class,
                () -> RateLimitedFeatureFlagReloadResult.allowed(0.0, null));

        RateLimitedFeatureFlagReloadResult allowed =
                RateLimitedFeatureFlagReloadResult.allowed(0.0, fingerprintResult);
        assertTrue(allowed.allowed());
    }

    private static RateLimitedFeatureFlagReloader reloader(
            FeatureFlagRegistry registry,
            ManualTimeSource clock,
            int capacity,
            double refillTokensPerSecond) {
        return new RateLimitedFeatureFlagReloader(
                "feature-flags",
                new TokenBucketRateLimiter(capacity, refillTokensPerSecond, clock),
                fingerprintingReloader(registry)
        );
    }

    private static FingerprintingFeatureFlagReloader fingerprintingReloader(FeatureFlagRegistry registry) {
        return new FingerprintingFeatureFlagReloader(
                registry,
                new FeatureFlagConfigFingerprinter(),
                new SafeFeatureFlagReloader(registry, new FeatureFlagReloadValidator(25, 10))
        );
    }
}

