package com.example.javalabs.basic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Learning double that keeps a notification call in flight until explicitly released.
 */
public final class BlockingNotificationClient implements NotificationClient {

    private final CountDownLatch started = new CountDownLatch(1);
    private final CountDownLatch release = new CountDownLatch(1);
    private int sentCount;

    /**
     * Blocks a welcome message until {@link #release()} is called.
     *
     * @param userProfile the user to notify
     * @throws IllegalArgumentException when {@code userProfile} is {@code null}
     * @throws IllegalStateException when the waiting thread is interrupted
     */
    @Override
    public void sendWelcomeMessage(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile must not be null");
        }
        started.countDown();
        try {
            release.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("notification interrupted", exception);
        }
        sentCount++;
    }

    /**
     * Waits until a send call has entered the blocking section.
     *
     * @param timeoutMillis maximum time to wait in milliseconds
     * @return {@code true} when a call started before the timeout
     * @throws InterruptedException when the current thread is interrupted while waiting
     */
    public boolean awaitStarted(long timeoutMillis) throws InterruptedException {
        return started.await(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Releases all blocked send calls.
     */
    public void release() {
        release.countDown();
    }

    /**
     * @return number of calls that completed after being released
     */
    public int sentCount() {
        return sentCount;
    }
}
