package com.example.javalabs.basic;

/**
 * Test-oriented clock that advances only when instructed.
 *
 * <p>Manual time removes sleeps from tests. Code that depends on time can move the clock forward
 * deterministically and assert exact boundary behavior.</p>
 */
public final class ManualTimeSource implements TimeSource {

    private long currentTimeMillis;

    /**
     * Creates a clock at a known timestamp.
     *
     * @param currentTimeMillis the initial timestamp in milliseconds
     */
    public ManualTimeSource(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long currentTimeMillis() {
        return currentTimeMillis;
    }

    /**
     * Moves the internal clock forward.
     *
     * @param millis the amount of time to add
     * @throws IllegalArgumentException when {@code millis} is negative
     */
    public void advanceMillis(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("millis must not be negative");
        }
        currentTimeMillis += millis;
    }
}
