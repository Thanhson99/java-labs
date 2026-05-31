package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Priority queue for background jobs.
 *
 * <p>Higher priority jobs are processed first. Jobs with the same priority keep FIFO order by
 * using an internal sequence number.</p>
 */
public final class PriorityJobQueue {

    private final PriorityQueue<QueuedJob> queue = new PriorityQueue<>(
            Comparator
                    .comparingInt((QueuedJob queuedJob) -> queuedJob.job().priority().weight())
                    .reversed()
                    // Preserve FIFO order for jobs with the same priority.
                    .thenComparingLong(QueuedJob::sequence)
    );
    private long nextSequence;

    /**
     * Adds a job to the queue.
     *
     * @param job job to enqueue
     * @throws IllegalArgumentException when {@code job} is {@code null}
     */
    public void enqueue(BackgroundJob job) {
        if (job == null) {
            throw new IllegalArgumentException("job must not be null");
        }
        queue.add(new QueuedJob(job, nextSequence++));
    }

    /**
     * Removes the next job by priority and FIFO tie-breaker.
     *
     * @return next job, or empty when the queue has no work
     */
    public Optional<BackgroundJob> poll() {
        QueuedJob queuedJob = queue.poll();
        if (queuedJob == null) {
            return Optional.empty();
        }
        return Optional.of(queuedJob.job());
    }

    /**
     * Removes up to {@code maxJobs} jobs in queue order.
     *
     * @param maxJobs maximum number of jobs to remove
     * @return drained jobs in processing order
     * @throws IllegalArgumentException when {@code maxJobs} is negative
     */
    public List<BackgroundJob> drain(int maxJobs) {
        if (maxJobs < 0) {
            throw new IllegalArgumentException("maxJobs must not be negative");
        }
        List<BackgroundJob> jobs = new ArrayList<>();
        while (jobs.size() < maxJobs && !queue.isEmpty()) {
            jobs.add(queue.poll().job());
        }
        return jobs;
    }

    /**
     * @return number of queued jobs
     */
    public int size() {
        return queue.size();
    }

    /**
     * @return {@code true} when no jobs are queued
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Internal queue entry that adds sequence metadata for FIFO tie-breaking.
     *
     * @param job queued job
     * @param sequence insertion sequence
     */
    private record QueuedJob(BackgroundJob job, long sequence) {
    }
}
