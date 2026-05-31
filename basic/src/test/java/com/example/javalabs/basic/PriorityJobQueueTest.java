package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PriorityJobQueueTest {

    @Test
    void pollsHigherPriorityJobsFirst() {
        PriorityJobQueue queue = new PriorityJobQueue();

        queue.enqueue(job("low-1", JobPriority.LOW));
        queue.enqueue(job("normal-1", JobPriority.NORMAL));
        queue.enqueue(job("high-1", JobPriority.HIGH));

        assertEquals("high-1", queue.poll().orElseThrow().id());
        assertEquals("normal-1", queue.poll().orElseThrow().id());
        assertEquals("low-1", queue.poll().orElseThrow().id());
    }

    @Test
    void keepsFifoOrderInsideSamePriority() {
        PriorityJobQueue queue = new PriorityJobQueue();

        queue.enqueue(job("high-1", JobPriority.HIGH));
        queue.enqueue(job("high-2", JobPriority.HIGH));
        queue.enqueue(job("high-3", JobPriority.HIGH));

        assertEquals("high-1", queue.poll().orElseThrow().id());
        assertEquals("high-2", queue.poll().orElseThrow().id());
        assertEquals("high-3", queue.poll().orElseThrow().id());
    }

    @Test
    void drainsUpToMaximumJobCount() {
        PriorityJobQueue queue = new PriorityJobQueue();

        queue.enqueue(job("low-1", JobPriority.LOW));
        queue.enqueue(job("high-1", JobPriority.HIGH));
        queue.enqueue(job("normal-1", JobPriority.NORMAL));

        List<BackgroundJob> drained = queue.drain(2);

        assertEquals(List.of(
                job("high-1", JobPriority.HIGH),
                job("normal-1", JobPriority.NORMAL)
        ), drained);
        assertEquals(1, queue.size());
    }

    @Test
    void emptyQueueReturnsEmptyOptional() {
        PriorityJobQueue queue = new PriorityJobQueue();

        assertTrue(queue.poll().isEmpty());
        assertTrue(queue.drain(10).isEmpty());
        assertTrue(queue.isEmpty());
    }

    @Test
    void rejectsInvalidInputs() {
        PriorityJobQueue queue = new PriorityJobQueue();

        assertThrows(IllegalArgumentException.class, () -> queue.enqueue(null));
        assertThrows(IllegalArgumentException.class, () -> queue.drain(-1));
        assertThrows(IllegalArgumentException.class, () -> new BackgroundJob("", "payload", JobPriority.HIGH));
        assertThrows(IllegalArgumentException.class, () -> new BackgroundJob("job-1", "", JobPriority.HIGH));
        assertThrows(IllegalArgumentException.class, () -> new BackgroundJob("job-1", "payload", null));
    }

    private static BackgroundJob job(String id, JobPriority priority) {
        return new BackgroundJob(id, "payload-" + id, priority);
    }
}
