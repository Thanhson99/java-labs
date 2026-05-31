package com.example.javalabs.basic;

import java.util.List;

/**
 * A Java record is a concise way to model immutable data.
 *
 * @param name the student name
 * @param score the student's overall score
 * @param topics the topics the student is practicing
 */
public record Student(String name, int score, List<String> topics) {

    /**
     * Adds basic validation to the generated record constructor.
     *
     * @throws IllegalArgumentException when the name, score, or topics collection is invalid
     */
    public Student {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("score must be between 0 and 100");
        }
        if (topics == null) {
            throw new IllegalArgumentException("topics must not be null");
        }
        // Copy the list so the record remains immutable even when callers mutate their original list.
        topics = List.copyOf(topics);
    }
}
