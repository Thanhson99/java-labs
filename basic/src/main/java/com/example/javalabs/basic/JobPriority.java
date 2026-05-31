package com.example.javalabs.basic;

/**
 * Priority level for background work.
 */
public enum JobPriority {
    /**
     * Most urgent background work.
     */
    HIGH(3),

    /**
     * Default background work priority.
     */
    NORMAL(2),

    /**
     * Best-effort background work.
     */
    LOW(1);

    private final int weight;

    /**
     * Creates a priority with a numeric scheduling weight.
     *
     * @param weight relative weight used by priority queues
     */
    JobPriority(int weight) {
        this.weight = weight;
    }

    /**
     * @return numeric scheduling weight; larger values run earlier
     */
    public int weight() {
        return weight;
    }
}
