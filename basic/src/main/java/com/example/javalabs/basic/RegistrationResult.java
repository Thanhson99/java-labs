package com.example.javalabs.basic;

/**
 * Result object returned by the registration service.
 *
 * @param accepted whether the request was accepted
 * @param message human-readable explanation of the outcome
 */
public record RegistrationResult(boolean accepted, String message) {
}
