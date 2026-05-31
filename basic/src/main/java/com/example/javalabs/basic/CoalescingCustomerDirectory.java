package com.example.javalabs.basic;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coalesces concurrent single-customer lookups for the same id.
 *
 * <p>If several threads ask for the same customer while the first lookup is still in flight, only
 * the first thread calls the delegate. The others wait for the same result.</p>
 */
public final class CoalescingCustomerDirectory implements CustomerDirectory {

    private final CustomerDirectory delegate;
    private final Map<String, CompletableFuture<Optional<Customer>>> inFlightLookups = new ConcurrentHashMap<>();

    /**
     * Creates a coalescing directory around a delegate.
     *
     * @param delegate source directory used for actual lookups
     * @throws IllegalArgumentException when {@code delegate} is {@code null}
     */
    public CoalescingCustomerDirectory(CustomerDirectory delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        this.delegate = delegate;
    }

    /**
     * Finds a customer while sharing duplicate in-flight work for the same id.
     *
     * @param id customer identifier
     * @return customer when present
     * @throws IllegalArgumentException when {@code id} is blank
     */
    @Override
    public Optional<Customer> findById(String id) {
        validateId(id);

        CompletableFuture<Optional<Customer>> existing = inFlightLookups.get(id);
        if (existing != null) {
            return join(existing);
        }

        CompletableFuture<Optional<Customer>> created = new CompletableFuture<>();
        CompletableFuture<Optional<Customer>> previous = inFlightLookups.putIfAbsent(id, created);
        if (previous != null) {
            return join(previous);
        }

        try {
            // Only the thread that wins putIfAbsent performs the delegate call.
            Optional<Customer> result = delegate.findById(id);
            created.complete(result);
            return result;
        } catch (RuntimeException exception) {
            created.completeExceptionally(exception);
            throw exception;
        } finally {
            inFlightLookups.remove(id, created);
        }
    }

    /**
     * Delegates batch lookups directly; this class focuses on coalescing single-id concurrent reads.
     *
     * @param ids customer identifiers to load
     * @return customers keyed by id
     */
    @Override
    public Map<String, Customer> findByIds(Collection<String> ids) {
        return delegate.findByIds(ids);
    }

    /**
     * @return number of single-customer lookups currently in flight
     */
    public int inFlightLookupCount() {
        return inFlightLookups.size();
    }

    /**
     * Waits for a shared lookup result and unwraps runtime failures.
     *
     * @param future shared lookup future
     * @return lookup result
     */
    private static Optional<Customer> join(CompletableFuture<Optional<Customer>> future) {
        try {
            return future.join();
        } catch (CompletionException exception) {
            if (exception.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw exception;
        }
    }

    /**
     * Validates a customer id.
     *
     * @param id customer identifier
     * @throws IllegalArgumentException when {@code id} is blank
     */
    private static void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
    }
}
