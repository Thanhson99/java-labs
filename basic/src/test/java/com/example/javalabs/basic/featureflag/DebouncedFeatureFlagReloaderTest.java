package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.TokenBucketRateLimiter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebouncedFeatureFlagReloaderTest {

    @Test
    void waitsUntilQuietPeriodBeforeReloading() {
        ManualTimeSource clock = new ManualTimeSource(1_000);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        DebouncedFeatureFlagReloader reloader = reloader(registry, clock, 500, 2);

        long dueAt = reloader.submit(List.of(new FeatureFlagRule("checkout", true, 20)));
        DebouncedFeatureFlagReloadResult early = reloader.flushIfDue();

        assertEquals(1_500, dueAt);
        assertTrue(early.waiting());
        assertEquals(new FeatureFlagRule("checkout", true, 10), registry.find("checkout").orElseThrow());

        clock.advanceMillis(500);
        DebouncedFeatureFlagReloadResult flushed = reloader.flushIfDue();

        assertTrue(flushed.flushed());
        assertTrue(flushed.reloadResult().orElseThrow().allowed());
        assertEquals(new FeatureFlagRule("checkout", true, 20), registry.find("checkout").orElseThrow());
        assertFalse(reloader.hasPendingReload());
    }

    @Test
    void coalescesRapidSubmissionsAndFlushesOnlyTheLatestConfig() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        DebouncedFeatureFlagReloader reloader = reloader(registry, clock, 1_000, 2);

        reloader.submit(List.of(new FeatureFlagRule("checkout", true, 20)));
        clock.advanceMillis(500);
        long newDueAt = reloader.submit(List.of(new FeatureFlagRule("checkout", true, 30)));

        clock.advanceMillis(500);
        assertTrue(reloader.flushIfDue().waiting());
        assertEquals(new FeatureFlagRule("checkout", true, 10), registry.find("checkout").orElseThrow());

        clock.advanceMillis(500);
        DebouncedFeatureFlagReloadResult flushed = reloader.flushIfDue();

        assertEquals(1_500, newDueAt);
        assertTrue(flushed.flushed());
        assertEquals(new FeatureFlagRule("checkout", true, 30), registry.find("checkout").orElseThrow());
    }

    @Test
    void returnsIdleWhenThereIsNoPendingConfig() {
        ManualTimeSource clock = new ManualTimeSource(2_000);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        DebouncedFeatureFlagReloader reloader = reloader(registry, clock, 500, 1);

        DebouncedFeatureFlagReloadResult result = reloader.flushIfDue();

        assertTrue(result.idle());
        assertEquals(2_000, result.dueAtMillis());
        assertTrue(result.reloadResult().isEmpty());
    }

    @Test
    void exposesBlockedRateLimitedReloadWhenFlushRunsTooSoonAfterPreviousFlush() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        DebouncedFeatureFlagReloader reloader = reloader(registry, clock, 100, 1);

        reloader.submit(List.of(new FeatureFlagRule("checkout", true, 20)));
        clock.advanceMillis(100);
        assertTrue(reloader.flushIfDue().reloadResult().orElseThrow().allowed());

        reloader.submit(List.of(new FeatureFlagRule("checkout", true, 25)));
        clock.advanceMillis(100);
        DebouncedFeatureFlagReloadResult blocked = reloader.flushIfDue();

        assertTrue(blocked.flushed());
        assertTrue(blocked.reloadResult().orElseThrow().blocked());
        assertEquals(new FeatureFlagRule("checkout", true, 20), registry.find("checkout").orElseThrow());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        ManualTimeSource clock = new ManualTimeSource(0);
        DebouncedFeatureFlagReloader reloader = reloader(registry, clock, 100, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new DebouncedFeatureFlagReloader(0, clock, rateLimitedReloader(registry, clock, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> new DebouncedFeatureFlagReloader(100, null, rateLimitedReloader(registry, clock, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> new DebouncedFeatureFlagReloader(100, clock, null));
        assertThrows(IllegalArgumentException.class, () -> reloader.submit(null));
        assertThrows(IllegalArgumentException.class, () -> reloader.submit(Arrays.asList(
                new FeatureFlagRule("checkout", true, 10),
                null
        )));
        assertThrows(IllegalArgumentException.class,
                () -> new DebouncedFeatureFlagReloadResult(null, 0, Optional.empty()));
        assertThrows(IllegalArgumentException.class,
                () -> new DebouncedFeatureFlagReloadResult(DebouncedReloadStatus.IDLE, -1, Optional.empty()));
        assertThrows(IllegalArgumentException.class,
                () -> new DebouncedFeatureFlagReloadResult(DebouncedReloadStatus.IDLE, 0, null));
        assertThrows(IllegalArgumentException.class,
                () -> DebouncedFeatureFlagReloadResult.flushed(0, null));
    }

    private static DebouncedFeatureFlagReloader reloader(
            FeatureFlagRegistry registry,
            ManualTimeSource clock,
            long quietPeriodMillis,
            int rateLimitCapacity) {
        return new DebouncedFeatureFlagReloader(
                quietPeriodMillis,
                clock,
                rateLimitedReloader(registry, clock, rateLimitCapacity)
        );
    }

    private static RateLimitedFeatureFlagReloader rateLimitedReloader(
            FeatureFlagRegistry registry,
            ManualTimeSource clock,
            int capacity) {
        FingerprintingFeatureFlagReloader fingerprintingReloader = new FingerprintingFeatureFlagReloader(
                registry,
                new FeatureFlagConfigFingerprinter(),
                new SafeFeatureFlagReloader(registry, new FeatureFlagReloadValidator(25, 10))
        );
        return new RateLimitedFeatureFlagReloader(
                "feature-flags",
                new TokenBucketRateLimiter(capacity, 1.0, clock),
                fingerprintingReloader
        );
    }
}

