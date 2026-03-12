package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DifficultyLevelTest {

    @Test
    void enumExposesStableMetadata() {
        assertEquals(1, DifficultyLevel.BEGINNER.order());
        assertTrue(DifficultyLevel.ADVANCED.studyAdvice().contains("file I/O"));
    }
}
