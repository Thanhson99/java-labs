package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgingPriorityJobQueueTest {

    @Test
    void highPriorityJobWinsWhenNoJobHasAged() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        AgingPriorityJobQueue queue = new AgingPriorityJobQueue(1_000, timeSource);

        queue.enqueue(job("low-1", JobPriority.LOW));
        queue.enqueue(job("high-1", JobPriority.HIGH));

        assertEquals("high-1", queue.poll().orElseThrow().id());
    }

    @Test
    void oldLowPriorityJobCanBeatNewHighPriorityJob() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        AgingPriorityJobQueue queue = new AgingPriorityJobQueue(1_000, timeSource);

        queue.enqueue(job("low-1", JobPriority.LOW));
        timeSource.advanceMillis(3_000);
        queue.enqueue(job("high-1", JobPriority.HIGH));

        assertEquals(4, queue.effectiveWeight("low-1"));
        assertEquals(3, queue.effectiveWeight("high-1"));
        assertEquals("low-1", queue.poll().orElseThrow().id());
    }

    @Test
    void keepsFifoWhenEffectiveWeightsAreEqual() {
        ManualTimeSource timeSource = new ManualTimeSource(0);
        AgingPriorityJobQueue queue = new AgingPriorityJobQueue(1_000, timeSource);

        queue.enqueue(job("normal-1", JobPriority.NORMAL));
        queue.enqueue(job("normal-2", JobPriority.NORMAL));

        assertEquals("normal-1", queue.poll().orElseThrow().id());
        assertEquals("normal-2", queue.poll().orElseThrow().id());
    }

    @Test
    void emptyQueueReturnsEmptyOptional() {
        AgingPriorityJobQueue queue = new AgingPriorityJobQueue(1_000, new ManualTimeSource(0));

        assertTrue(queue.poll().isEmpty());
        assertTrue(queue.isEmpty());
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new AgingPriorityJobQueue(0, new ManualTimeSource(0)));
        assertThrows(IllegalArgumentException.class, () -> new AgingPriorityJobQueue(1_000, null));

        AgingPriorityJobQueue queue = new AgingPriorityJobQueue(1_000, new ManualTimeSource(0));
        assertThrows(IllegalArgumentException.class, () -> queue.enqueue(null));
        assertThrows(IllegalArgumentException.class, () -> queue.effectiveWeight(" "));
        assertThrows(IllegalStateException.class, () -> queue.effectiveWeight("missing"));
    }

    private static BackgroundJob job(String id, JobPriority priority) {
        return new BackgroundJob(id, "payload-" + id, priority);
    }
}
