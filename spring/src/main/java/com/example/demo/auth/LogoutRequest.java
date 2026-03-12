package com.example.demo.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request used to revoke a refresh token during logout.
 */
public record LogoutRequest(@NotBlank String refreshToken) {
}
