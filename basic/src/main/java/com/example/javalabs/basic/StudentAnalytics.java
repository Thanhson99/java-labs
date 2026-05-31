package com.example.javalabs.basic;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Demonstrates collection processing with streams and comparators.
 */
public final class StudentAnalytics {

    /**
     * Utility class; instances are not needed because every analysis method is static.
     */
    private StudentAnalytics() {
    }

    /**
     * Finds the highest-scoring student.
     *
     * @param students the input list, which must not be empty
     * @return the student with the maximum score
     * @throws IllegalArgumentException when {@code students} is {@code null} or empty
     */
    public static Student findTopStudent(List<Student> students) {
        validateStudents(students);
        return students.stream()
                .max(Comparator.comparingInt(Student::score))
                .orElseThrow();
    }

    /**
     * Computes the arithmetic mean of all scores.
     *
     * @param students the students to analyze
     * @return the average score as a double
     * @throws IllegalArgumentException when {@code students} is {@code null} or empty
     */
    public static double averageScore(List<Student> students) {
        validateStudents(students);
        return students.stream()
                .mapToInt(Student::score)
                .average()
                .orElseThrow();
    }

    /**
     * Filters students by topic and returns their names in alphabetical order.
     *
     * @param students the source students
     * @param topic the topic to match, case-insensitively
     * @return matching student names
     * @throws IllegalArgumentException when {@code students} is empty or {@code topic} is blank
     */
    public static List<String> filterByTopic(List<Student> students, String topic) {
        validateStudents(students);
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("topic must not be blank");
        }

        String normalizedTopic = topic.toLowerCase(Locale.ROOT);
        return students.stream()
                .filter(student -> student.topics().stream()
                        // Normalize each stored topic so callers can search without matching case exactly.
                        .map(value -> value.toLowerCase(Locale.ROOT))
                        .anyMatch(normalizedTopic::equals))
                .map(Student::name)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Validates the common input contract for student analytics.
     *
     * @param students student list to validate
     * @throws IllegalArgumentException when the list is {@code null} or empty
     */
    private static void validateStudents(List<Student> students) {
        if (students == null || students.isEmpty()) {
            throw new IllegalArgumentException("students must not be empty");
        }
    }
}
