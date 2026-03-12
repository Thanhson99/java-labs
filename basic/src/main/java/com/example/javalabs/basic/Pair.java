package com.example.javalabs.basic;

/**
 * Generic classes allow the same structure to work with many different types.
 *
 * @param left the first value
 * @param right the second value
 * @param <L> the left type
 * @param <R> the right type
 */
public record Pair<L, R>(L left, R right) {

    /**
     * Factory method used when type inference makes code easier to read.
     *
     * @param left the left value
     * @param right the right value
     * @param <L> the left type
     * @param <R> the right type
     * @return a new pair instance
     */
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }
}
