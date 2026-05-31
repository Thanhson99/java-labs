package com.example.javalabs.basic;

/**
 * Production implementation backed by the system clock.
 *
 * <p>This implementation is intentionally tiny. Keeping it behind {@link TimeSource} allows
 * production code to use real time while tests use {@link ManualTimeSource}.</p>
 */
public final class SystemTimeSource implements TimeSource {

    /**
     * {@inheritDoc}
     */
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
