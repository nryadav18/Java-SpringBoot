package com.course.shopeasy.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Body of POST /api/v1/auth/register. A record = immutable DTO with almost no code (topic 15 / 33).
 * The validation annotations are checked because the controller uses @Valid (topic 34).
 *
 * Note there is NO "role" field: anybody who registers is a CUSTOMER. Letting the client choose
 * its own role would let anyone make themselves ADMIN (a classic security hole: "mass assignment").
 */
public record RegisterRequest(
        @Schema(example = "Asha Verma")
        @NotBlank @Size(max = 100)
        String fullName,

        @Schema(example = "asha@example.com")
        @NotBlank @Email @Size(max = 150)
        String email,

        @Schema(example = "Secret@123")
        @NotBlank
        @Size(min = 8, max = 72, message = "must be between 8 and 72 characters")   // BCrypt uses max 72 bytes
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "must contain at least one letter and one digit")
        String password) {
}
