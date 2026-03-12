package com.example.javalabs.basic;

/**
 * Demonstrates methods that rely on conditionals, loops, and input validation.
 */
public final class ControlFlowExamples {

    private ControlFlowExamples() {
    }

    /**
     * Classifies a number as positive, negative, or zero.
     *
     * @param number the input value
     * @return a human-readable category for the number
     */
    public static String classifyNumber(int number) {
        if (number > 0) {
            return "positive";
        }
        if (number < 0) {
            return "negative";
        }
        return "zero";
    }

    /**
     * Sums all even integers from 0 up to and including the provided limit.
     *
     * @param limit the upper bound, which must not be negative
     * @return the sum of all even numbers within the range
     */
    public static int sumEvenNumbers(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit must be non-negative");
        }

        int sum = 0;
        for (int value = 0; value <= limit; value++) {
            if (value % 2 == 0) {
                sum += value;
            }
        }
        return sum;
    }

    /**
     * Calculates a factorial using an iterative loop.
     *
     * <p>This version is intentionally iterative because it is easier to debug for beginners and
     * avoids the extra stack usage of recursion.</p>
     *
     * @param number a number from 0 upward
     * @return the factorial of the input
     */
    public static long factorial(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("number must be non-negative");
        }

        long result = 1;
        for (int current = 2; current <= number; current++) {
            result *= current;
        }
        return result;
    }
}
