package com.course.files.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * Type-safe settings for file storage (topic 29), read from application.properties:
 *
 *   app.storage.location=uploads
 *   app.storage.allowed-extensions=jpg,jpeg,png,gif,webp,pdf,txt,csv
 *
 * WHY a whitelist of extensions?  Accepting ANY file means someone can upload
 * evil.html or evil.svg (with JavaScript inside) or virus.exe. Only allow what your feature needs.
 */
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(String location, Set<String> allowedExtensions) {
}
