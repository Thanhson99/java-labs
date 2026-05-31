package com.example.javalabs.basic;

import java.util.concurrent.CompletableFuture;

/**
 * Demonstrates asynchronous composition with {@link CompletableFuture}.
 */
public final class AsyncExamples {

    /**
     * Utility class; instances are not needed because the async examples are static.
     */
    private AsyncExamples() {
    }

    /**
     * Builds a small asynchronous report in two steps.
     *
     * @param userName the user to greet
     * @return a future that completes with a formatted report
     * @throws IllegalArgumentException when {@code userName} is blank
     */
    public static CompletableFuture<String> buildUserReport(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("userName must not be blank");
        }

        return fetchGreeting(userName)
                // Run the independent greeting and score futures, then combine their values.
                .thenCombine(fetchScore(userName), (greeting, score) ->
                        greeting + " Your practice score is " + score + ".");
    }

    /**
     * Simulates an asynchronous service call that returns a greeting.
     */
    private static CompletableFuture<String> fetchGreeting(String userName) {
        return CompletableFuture.supplyAsync(() -> "Hello, %s!".formatted(userName));
    }

    /**
     * Simulates an asynchronous service call that returns a practice score.
     */
    private static CompletableFuture<Integer> fetchScore(String userName) {
        return CompletableFuture.supplyAsync(() -> userName.length() * 10);
    }
}
