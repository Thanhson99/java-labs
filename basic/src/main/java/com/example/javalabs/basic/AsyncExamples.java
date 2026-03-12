package com.example.javalabs.basic;

import java.util.concurrent.CompletableFuture;

/**
 * Demonstrates asynchronous composition with {@link CompletableFuture}.
 */
public final class AsyncExamples {

    private AsyncExamples() {
    }

    /**
     * Builds a small asynchronous report in two steps.
     *
     * @param userName the user to greet
     * @return a future that completes with a formatted report
     */
    public static CompletableFuture<String> buildUserReport(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("userName must not be blank");
        }

        return fetchGreeting(userName)
                .thenCombine(fetchScore(userName), (greeting, score) ->
                        greeting + " Your practice score is " + score + ".");
    }

    private static CompletableFuture<String> fetchGreeting(String userName) {
        return CompletableFuture.supplyAsync(() -> "Hello, %s!".formatted(userName));
    }

    private static CompletableFuture<Integer> fetchScore(String userName) {
        return CompletableFuture.supplyAsync(() -> userName.length() * 10);
    }
}
