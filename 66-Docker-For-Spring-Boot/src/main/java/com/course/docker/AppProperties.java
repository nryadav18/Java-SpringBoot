/*
 * AppProperties - type-safe settings under the "app." prefix (topic 29).
 *
 * In application.yml:          app.greeting: Hello from a laptop
 * As an ENVIRONMENT VARIABLE:  APP_GREETING=Hello from Docker
 *
 * Spring Boot's "relaxed binding" converts env-var names automatically:
 *   upper-case, dots become underscores, dashes are removed
 *   app.greeting          <-  APP_GREETING
 *   app.environment-name  <-  APP_ENVIRONMENTNAME
 *   server.port           <-  SERVER_PORT
 *   spring.profiles.active<-  SPRING_PROFILES_ACTIVE
 * Environment variables WIN over application.yml - that is how one image runs in dev, test and prod.
 */
package com.course.docker;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(String greeting, String environmentName) {
}
