package com.example.demo.registration;

/**
 * Service-layer outcome for a registration attempt.
 */
public record RegistrationResult(boolean accepted, String message, UserProfileResponse userProfile) {
}
