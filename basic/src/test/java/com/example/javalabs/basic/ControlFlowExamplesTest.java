package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ControlFlowExamplesTest {

    @Test
    void classifyNumberDistinguishesPositiveNegativeAndZero() {
        assertEquals("positive", ControlFlowExamples.classifyNumber(3));
        assertEquals("negative", ControlFlowExamples.classifyNumber(-1));
        assertEquals("zero", ControlFlowExamples.classifyNumber(0));
    }

    @Test
    void sumEvenNumbersAccumulatesOnlyEvenValues() {
        assertEquals(30, ControlFlowExamples.sumEvenNumbers(10));
    }

    @Test
    void sumEvenNumbersRejectsNegativeLimit() {
        assertThrows(IllegalArgumentException.class, () -> ControlFlowExamples.sumEvenNumbers(-1));
    }

    @Test
    void factorialComputesExpectedValue() {
        assertEquals(120L, ControlFlowExamples.factorial(5));
        assertEquals(1L, ControlFlowExamples.factorial(0));
    }
}
