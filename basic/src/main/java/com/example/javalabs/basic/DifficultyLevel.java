package com.example.javalabs.basic;

/**
 * Models the recommended learning depth for each practice stage.
 *
 * <p>An enum is useful here because the valid values are finite, named, and stable. Each value
 * carries small metadata so callers do not need a separate lookup table.</p>
 */
public enum DifficultyLevel {
    /**
     * First-pass exercises focused on syntax, control flow, and small objects.
     */
    BEGINNER(1, "Start with guided examples."),

    /**
     * Practice that combines multiple classes, collections, and service boundaries.
     */
    INTERMEDIATE(2, "Work with collections, objects, and streams."),

    /**
     * More realistic backend patterns that require reading state, failure paths, and tradeoffs.
     */
    ADVANCED(3, "Study asynchronous code, file I/O, and design choices.");

    private final int order;
    private final String studyAdvice;

    /**
     * Creates an enum constant with display metadata.
     *
     * @param order display order for the learning path
     * @param studyAdvice short advice shown to learners
     */
    DifficultyLevel(int order, String studyAdvice) {
        this.order = order;
        this.studyAdvice = studyAdvice;
    }

    /**
     * Returns the display order used by learning pages and reports.
     *
     * @return a positive ordering number, where smaller values are easier levels
     */
    public int order() {
        return order;
    }

    /**
     * Returns a short recommendation for how to study this level.
     *
     * @return learning advice suitable for display in the console or site UI
     */
    public String studyAdvice() {
        return studyAdvice;
    }
}
