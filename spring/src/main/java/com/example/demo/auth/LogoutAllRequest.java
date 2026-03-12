package com.example.demo.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Request used to revoke every refresh token for a user after re-authentication.
 */
public record LogoutAllRequest(
        @NotBlank String username,
        @NotBlank String password) {
}
