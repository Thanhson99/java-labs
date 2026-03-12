package com.example.javalabs.basic;

/**
 * Production implementation backed by the system clock.
 */
public final class SystemTimeSource implements TimeSource {

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
