package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Priority job queue with aging to reduce starvation.
 *
 * <p>A normal priority queue can leave low-priority work waiting for a long time when high-priority
 * work keeps arriving. Aging increases a job's effective priority as it waits.</p>
 */
public final class AgingPriorityJobQueue {

    private final long agingIntervalMillis;
    private final TimeSource timeSource;
    private final List<QueuedJob> jobs = new ArrayList<>();
    private long nextSequence;

    /**
     * Creates an aging priority queue.
     *
     * @param agingIntervalMillis time a job must wait before gaining one priority point
     * @param timeSource clock used to make aging deterministic in tests
     * @throws IllegalArgumentException when interval or clock is invalid
     */
    public AgingPriorityJobQueue(long agingIntervalMillis, TimeSource timeSource) {
        if (agingIntervalMillis <= 0) {
            throw new IllegalArgumentException("agingIntervalMillis must be positive");
        }
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.agingIntervalMillis = agingIntervalMillis;
        this.timeSource = timeSource;
    }

    /**
     * Adds a job with the current enqueue timestamp.
     *
     * @param job job to enqueue
     * @throws IllegalArgumentException when {@code job} is {@code null}
     */
    public void enqueue(BackgroundJob job) {
        if (job == null) {
            throw new IllegalArgumentException("job must not be null");
        }
        jobs.add(new QueuedJob(job, timeSource.currentTimeMillis(), nextSequence++));
    }

    /**
     * Removes the job with the highest effective priority.
     *
     * @return selected job, or empty when no jobs are queued
     */
    public Optional<BackgroundJob> poll() {
        if (jobs.isEmpty()) {
            return Optional.empty();
        }

        long now = timeSource.currentTimeMillis();
        QueuedJob selected = jobs.stream()
                .max(Comparator
                        .comparingInt((QueuedJob job) -> effectiveWeight(job, now))
                        // Lower sequence means earlier enqueue; negate it so earlier jobs win ties in max().
                        .thenComparing((QueuedJob job) -> -job.sequence()))
                .orElseThrow();
        jobs.remove(selected);
        return Optional.of(selected.job());
    }

    /**
     * Calculates the current effective weight for a queued job id.
     *
     * @param jobId job identifier
     * @return current priority weight after aging boost
     * @throws IllegalArgumentException when {@code jobId} is blank
     * @throws IllegalStateException when the job is not queued
     */
    public int effectiveWeight(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("jobId must not be blank");
        }
        long now = timeSource.currentTimeMillis();
        return jobs.stream()
                .filter(job -> job.job().id().equals(jobId))
                .findFirst()
                .map(job -> effectiveWeight(job, now))
                .orElseThrow(() -> new IllegalStateException("job not found: " + jobId));
    }

    /**
     * @return number of queued jobs
     */
    public int size() {
        return jobs.size();
    }

    /**
     * @return {@code true} when no jobs are queued
     */
    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    /**
     * Calculates base priority plus wait-time boost.
     *
     * @param queuedJob queued job entry
     * @param now current timestamp in milliseconds
     * @return effective priority weight capped at {@link Integer#MAX_VALUE}
     */
    private int effectiveWeight(QueuedJob queuedJob, long now) {
        long waitedMillis = Math.max(0, now - queuedJob.enqueuedAtMillis());
        long agingBoost = waitedMillis / agingIntervalMillis;
        long effectiveWeight = queuedJob.job().priority().weight() + agingBoost;
        return effectiveWeight > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) effectiveWeight;
    }

    /**
     * Internal queue entry with timestamp and sequence metadata.
     *
     * @param job queued job
     * @param enqueuedAtMillis enqueue timestamp
     * @param sequence insertion sequence
     */
    private record QueuedJob(BackgroundJob job, long enqueuedAtMillis, long sequence) {
    }
}
