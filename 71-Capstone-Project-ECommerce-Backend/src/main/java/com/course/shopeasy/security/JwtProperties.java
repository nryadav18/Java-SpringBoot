package com.course.shopeasy.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Type-safe JWT settings, bound from the "shopeasy.jwt.*" keys in application.yml (topic 29).
 *
 * <pre>
 * shopeasy:
 *   jwt:
 *     secret: ${JWT_SECRET:...}   # Base64, at least 256 bits (32 bytes) for HS256
 *     expiration: 1h              # Spring converts "1h", "30m", "15s" into a Duration
 *     issuer: shopeasy
 * </pre>
 *
 * @Validated makes the app FAIL AT STARTUP if the secret is missing - much better than failing at the first login.
 */
@Validated
@ConfigurationProperties(prefix = "shopeasy.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @NotNull Duration expiration,
        @NotBlank String issuer) {
}
