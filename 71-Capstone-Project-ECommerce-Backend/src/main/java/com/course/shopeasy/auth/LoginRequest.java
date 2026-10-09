package com.course.shopeasy.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Body of POST /api/v1/auth/login. */
public record LoginRequest(
        @Schema(example = "customer@shopeasy.com")
        @NotBlank @Email
        String email,

        @Schema(example = "Customer@123")
        @NotBlank
        String password) {
}
