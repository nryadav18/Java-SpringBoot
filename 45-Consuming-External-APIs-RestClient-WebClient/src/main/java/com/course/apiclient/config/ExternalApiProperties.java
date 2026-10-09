package com.course.apiclient.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/*
 * Type-safe settings for the external API (topic 29).
 *
 * application.properties:
 *   external.jsonplaceholder.base-url=https://jsonplaceholder.typicode.com
 *   external.jsonplaceholder.connect-timeout=3s
 *   external.jsonplaceholder.read-timeout=5s
 *
 * Spring converts "3s" into a Duration for us. @Validated makes the app fail
 * at startup if the base URL is missing - much better than failing at the first call.
 */
@Validated
@ConfigurationProperties(prefix = "external.jsonplaceholder")
public record ExternalApiProperties(
        @NotBlank String baseUrl,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout) {
}
