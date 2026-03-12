package com.example.demo.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Login request used to exchange demo credentials for a JWT token.
 */
public record TokenRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
