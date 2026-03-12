package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionPlaygroundTest {

    @Test
    void safeDivideReturnsQuotient() {
        assertEquals(2.5, ExceptionPlayground.safeDivide(5, 2));
    }

    @Test
    void safeDivideRejectsZeroDivisor() {
        assertThrows(IllegalArgumentException.class, () -> ExceptionPlayground.safeDivide(10, 0));
    }

    @Test
    void parsePositiveIntValidatesInput() {
        assertEquals(42, ExceptionPlayground.parsePositiveInt("42"));
        assertThrows(IllegalArgumentException.class, () -> ExceptionPlayground.parsePositiveInt("abc"));
        assertThrows(IllegalArgumentException.class, () -> ExceptionPlayground.parsePositiveInt("0"));
    }
}
