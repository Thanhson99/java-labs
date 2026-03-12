package com.example.demo.registration;

import com.example.demo.profile.Region;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload accepted by the registration API.
 */
public record RegisterUserRequest(
        @NotBlank String userId,
        @NotBlank @Email String email,
        @NotNull Region region
) {
}
