package com.example.javalabs.basic.featureflag;

import com.example.javalabs.basic.ManualTimeSource;
import com.example.javalabs.basic.TokenBucketRateLimiter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstrumentedDebouncedFeatureFlagReloaderTest {

    @Test
    void recordsIdleWaitingAndAppliedReloads() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        InstrumentedDebouncedFeatureFlagReloader reloader = instrumented(registry, clock, 100, 2);

        assertTrue(reloader.flushIfDue().idle());

        reloader.submit(List.of(new FeatureFlagRule("checkout", true, 20)));
        assertTrue(reloader.flushIfDue().waiting());
        clock.advanceMillis(100);
        assertTrue(reloader.flushIfDue().flushed());

        FeatureFlagReloadMetricsSnapshot snapshot = reloader.metricsSnapshot();
        assertEquals(1, snapshot.submissions());
        assertEquals(1, snapshot.idleFlushes());
        assertEquals(1, snapshot.waitingFlushes());
        assertEquals(1, snapshot.flushedAttempts());
        assertEquals(1, snapshot.safeReloadApplied());
        assertEquals(2, snapshot.completedWithoutMutation());
        assertEquals(new FeatureFlagRule("checkout", true, 20), registry.find("checkout").orElseThrow());
    }

    @Test
    void recordsFingerprintSkipsAndRateLimitedBlocks() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        InstrumentedDebouncedFeatureFlagReloader reloader = instrumented(registry, clock, 100, 1);

        reloader.submit(List.of(new FeatureFlagRule("checkout", true, 10)));
        clock.advanceMillis(100);
        reloader.flushIfDue();

        reloader.submit(List.of(new FeatureFlagRule("checkout", true, 20)));
        clock.advanceMillis(100);
        reloader.flushIfDue();

        FeatureFlagReloadMetricsSnapshot snapshot = reloader.metricsSnapshot();
        assertEquals(2, snapshot.submissions());
        assertEquals(2, snapshot.flushedAttempts());
        assertEquals(1, snapshot.fingerprintSkips());
        assertEquals(1, snapshot.rateLimitedBlocks());
        assertEquals(2, snapshot.completedWithoutMutation());
        assertEquals(new FeatureFlagRule("checkout", true, 10), registry.find("checkout").orElseThrow());
    }

    @Test
    void recordsRejectedSafeReloads() {
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of(
                new FeatureFlagRule("checkout", true, 10)
        ));
        InstrumentedDebouncedFeatureFlagReloader reloader = instrumented(registry, clock, 100, 2);

        reloader.submit(List.of(new FeatureFlagRule("checkout", true, 90)));
        clock.advanceMillis(100);
        reloader.flushIfDue();

        FeatureFlagReloadMetricsSnapshot snapshot = reloader.metricsSnapshot();
        assertEquals(1, snapshot.safeReloadRejected());
        assertEquals(1, snapshot.completedWithoutMutation());
        assertEquals(new FeatureFlagRule("checkout", true, 10), registry.find("checkout").orElseThrow());
    }

    @Test
    void rejectsInvalidInputs() {
        FeatureFlagReloadMetrics metrics = new FeatureFlagReloadMetrics();
        ManualTimeSource clock = new ManualTimeSource(0);
        FeatureFlagRegistry registry = new FeatureFlagRegistry(List.of());
        DebouncedFeatureFlagReloader delegate = debounced(registry, clock, 100, 1);

        assertThrows(IllegalArgumentException.class,
                () -> new InstrumentedDebouncedFeatureFlagReloader(null, metrics));
        assertThrows(IllegalArgumentException.class,
                () -> new InstrumentedDebouncedFeatureFlagReloader(delegate, null));
        assertThrows(IllegalArgumentException.class, () -> metrics.recordFlushResult(null));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureFlagReloadMetricsSnapshot(-1, 0, 0, 0, 0, 0, 0, 0));
    }

    private static InstrumentedDebouncedFeatureFlagReloader instrumented(
            FeatureFlagRegistry registry,
            ManualTimeSource clock,
            long quietPeriodMillis,
            int rateLimitCapacity) {
        return new InstrumentedDebouncedFeatureFlagReloader(
                debounced(registry, clock, quietPeriodMillis, rateLimitCapacity),
                new FeatureFlagReloadMetrics()
        );
    }

    private static DebouncedFeatureFlagReloader debounced(
            FeatureFlagRegistry registry,
            ManualTimeSource clock,
            long quietPeriodMillis,
            int rateLimitCapacity) {
        FingerprintingFeatureFlagReloader fingerprintingReloader = new FingerprintingFeatureFlagReloader(
                registry,
                new FeatureFlagConfigFingerprinter(),
                new SafeFeatureFlagReloader(registry, new FeatureFlagReloadValidator(25, 10))
        );
        return new DebouncedFeatureFlagReloader(
                quietPeriodMillis,
                clock,
                new RateLimitedFeatureFlagReloader(
                        "feature-flags",
                        new TokenBucketRateLimiter(rateLimitCapacity, 1.0, clock),
                        fingerprintingReloader
                )
        );
    }
}

