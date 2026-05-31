package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Queue that schedules jobs by earliest deadline first.
 *
 * <p>Deadline-based scheduling is useful when the important question is not just priority, but
 * which job is closest to breaching its time budget.</p>
 */
public final class DeadlineJobQueue {

    private final PriorityQueue<QueuedDeadlineJob> queue = new PriorityQueue<>(
            Comparator
                    .comparingLong((QueuedDeadlineJob queuedJob) -> queuedJob.deadlineJob().deadlineMillis())
                    // Earlier deadlines win; same deadline prefers higher priority work.
                    .thenComparing((QueuedDeadlineJob queuedJob) -> -queuedJob.deadlineJob().job().priority().weight())
                    .thenComparingLong(QueuedDeadlineJob::sequence)
    );
    private final TimeSource timeSource;
    private long nextSequence;

    /**
     * Creates a deadline queue.
     *
     * @param timeSource clock used for overdue and remaining-time calculations
     * @throws IllegalArgumentException when {@code timeSource} is {@code null}
     */
    public DeadlineJobQueue(TimeSource timeSource) {
        if (timeSource == null) {
            throw new IllegalArgumentException("timeSource must not be null");
        }
        this.timeSource = timeSource;
    }

    /**
     * Adds a deadline job.
     *
     * @param deadlineJob job with absolute deadline
     * @throws IllegalArgumentException when {@code deadlineJob} is {@code null}
     */
    public void enqueue(DeadlineJob deadlineJob) {
        if (deadlineJob == null) {
            throw new IllegalArgumentException("deadlineJob must not be null");
        }
        queue.add(new QueuedDeadlineJob(deadlineJob, nextSequence++));
    }

    /**
     * Removes the next job by earliest deadline.
     *
     * @return next deadline job, or empty when no work is queued
     */
    public Optional<DeadlineJob> poll() {
        QueuedDeadlineJob queuedJob = queue.poll();
        if (queuedJob == null) {
            return Optional.empty();
        }
        return Optional.of(queuedJob.deadlineJob());
    }

    /**
     * Returns queued jobs whose deadlines have passed.
     *
     * @return overdue jobs sorted by deadline
     */
    public List<DeadlineJob> overdueJobs() {
        long now = timeSource.currentTimeMillis();
        return queue.stream()
                .map(QueuedDeadlineJob::deadlineJob)
                .filter(job -> job.deadlineMillis() <= now)
                .sorted(Comparator.comparingLong(DeadlineJob::deadlineMillis))
                .toList();
    }

    /**
     * Calculates time until the next queued deadline.
     *
     * @return milliseconds until next deadline, {@code 0} when overdue, or {@code -1} when empty
     */
    public long millisUntilNextDeadline() {
        QueuedDeadlineJob next = queue.peek();
        if (next == null) {
            return -1;
        }
        return Math.max(0, next.deadlineJob().deadlineMillis() - timeSource.currentTimeMillis());
    }

    /**
     * Removes up to {@code maxJobs} deadline jobs in processing order.
     *
     * @param maxJobs maximum number of jobs to remove
     * @return drained jobs in processing order
     * @throws IllegalArgumentException when {@code maxJobs} is negative
     */
    public List<DeadlineJob> drain(int maxJobs) {
        if (maxJobs < 0) {
            throw new IllegalArgumentException("maxJobs must not be negative");
        }
        List<DeadlineJob> jobs = new ArrayList<>();
        while (jobs.size() < maxJobs && !queue.isEmpty()) {
            jobs.add(queue.poll().deadlineJob());
        }
        return jobs;
    }

    /**
     * @return number of queued deadline jobs
     */
    public int size() {
        return queue.size();
    }

    /**
     * Internal queue entry that keeps FIFO tie-breaking metadata.
     *
     * @param deadlineJob queued deadline job
     * @param sequence insertion sequence
     */
    private record QueuedDeadlineJob(DeadlineJob deadlineJob, long sequence) {
    }
}
