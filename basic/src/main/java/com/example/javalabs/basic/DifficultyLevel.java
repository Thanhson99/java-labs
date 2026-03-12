package com.example.javalabs.basic;

/**
 * Enum values model a fixed set of named constants.
 */
public enum DifficultyLevel {
    BEGINNER(1, "Start with guided examples."),
    INTERMEDIATE(2, "Work with collections, objects, and streams."),
    ADVANCED(3, "Study asynchronous code, file I/O, and design choices.");

    private final int order;
    private final String studyAdvice;

    DifficultyLevel(int order, String studyAdvice) {
        this.order = order;
        this.studyAdvice = studyAdvice;
    }

    public int order() {
        return order;
    }

    public String studyAdvice() {
        return studyAdvice;
    }
}
