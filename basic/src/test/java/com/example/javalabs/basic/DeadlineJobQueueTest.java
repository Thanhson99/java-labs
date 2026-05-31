package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeadlineJobQueueTest {

    @Test
    void pollsEarliestDeadlineFirst() {
        DeadlineJobQueue queue = new DeadlineJobQueue(new ManualTimeSource(0));

        queue.enqueue(deadlineJob("later", JobPriority.HIGH, 5_000));
        queue.enqueue(deadlineJob("soon", JobPriority.LOW, 1_000));
        queue.enqueue(deadlineJob("middle", JobPriority.NORMAL, 3_000));

        assertEquals("soon", queue.poll().orElseThrow().job().id());
        assertEquals("middle", queue.poll().orElseThrow().job().id());
        assertEquals("later", queue.poll().orElseThrow().job().id());
    }

    @Test
    void usesPriorityAsTieBreakerForSameDeadline() {
        DeadlineJobQueue queue = new DeadlineJobQueue(new ManualTimeSource(0));

        queue.enqueue(deadlineJob("low", JobPriority.LOW, 1_000));
        queue.enqueue(deadlineJob("high", JobPriority.HIGH, 1_000));
        queue.enqueue(deadlineJob("normal", JobPriority.NORMAL, 1_000));

        assertEquals("high", queue.poll().orElseThrow().job().id());
        assertEquals("normal", queue.poll().orElseThrow().job().id());
        assertEquals("low", queue.poll().orElseThrow().job().id());
    }

    @Test
    void reportsOverdueJobsAndTimeUntilNextDeadline() {
        ManualTimeSource timeSource = new ManualTimeSource(2_000);
        DeadlineJobQueue queue = new DeadlineJobQueue(timeSource);

        DeadlineJob overdue = deadlineJob("overdue", JobPriority.NORMAL, 1_000);
        DeadlineJob future = deadlineJob("future", JobPriority.NORMAL, 3_500);
        queue.enqueue(future);
        queue.enqueue(overdue);

        assertEquals(List.of(overdue), queue.overdueJobs());
        assertEquals(0, queue.millisUntilNextDeadline());
        queue.poll();
        assertEquals(1_500, queue.millisUntilNextDeadline());
    }

    @Test
    void emptyQueueReturnsEmptyResults() {
        DeadlineJobQueue queue = new DeadlineJobQueue(new ManualTimeSource(0));

        assertTrue(queue.poll().isEmpty());
        assertTrue(queue.overdueJobs().isEmpty());
        assertEquals(-1, queue.millisUntilNextDeadline());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new DeadlineJobQueue(null));

        DeadlineJobQueue queue = new DeadlineJobQueue(new ManualTimeSource(0));
        assertThrows(IllegalArgumentException.class, () -> queue.enqueue(null));
        assertThrows(IllegalArgumentException.class, () -> queue.drain(-1));
        assertThrows(IllegalArgumentException.class,
                () -> new DeadlineJob(new BackgroundJob("job", "payload", JobPriority.NORMAL), -1));
    }

    private static DeadlineJob deadlineJob(String id, JobPriority priority, long deadlineMillis) {
        return new DeadlineJob(new BackgroundJob(id, "payload-" + id, priority), deadlineMillis);
    }
}
