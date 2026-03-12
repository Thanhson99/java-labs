package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentAnalyticsTest {

    private final List<Student> students = List.of(
            new Student("Alice", 92, List.of("Java", "SQL")),
            new Student("Bob", 77, List.of("Spring")),
            new Student("Cara", 88, List.of("Java", "Testing", "Docker"))
    );

    @Test
    void findsTopStudent() {
        assertEquals("Alice", StudentAnalytics.findTopStudent(students).name());
    }

    @Test
    void computesAverageScore() {
        assertEquals((92.0 + 77.0 + 88.0) / 3.0, StudentAnalytics.averageScore(students));
    }

    @Test
    void filtersStudentsByTopicCaseInsensitively() {
        assertEquals(List.of("Alice", "Cara"), StudentAnalytics.filterByTopic(students, "java"));
    }
}
