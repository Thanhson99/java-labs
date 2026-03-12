package com.example.javalabs.basic;

/**
 * Test-oriented clock that advances only when instructed.
 */
public final class ManualTimeSource implements TimeSource {

    private long currentTimeMillis;

    public ManualTimeSource(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    @Override
    public long currentTimeMillis() {
        return currentTimeMillis;
    }

    /**
     * Moves the internal clock forward.
     *
     * @param millis the amount of time to add
     */
    public void advanceMillis(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("millis must not be negative");
        }
        currentTimeMillis += millis;
    }
}
