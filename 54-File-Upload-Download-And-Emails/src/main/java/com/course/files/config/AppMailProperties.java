package com.course.files.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * Our own mail settings (the SMTP server itself is configured with Spring Boot's spring.mail.* properties).
 *
 *   app.mail.from=no-reply@shop.example
 */
@ConfigurationProperties(prefix = "app.mail")
public record AppMailProperties(String from) {
}
