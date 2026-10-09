package com.course.shopeasy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * shopeasy.seed.enabled=true|false  - whether DataSeeder creates the demo users at startup.
 * In a real production system you would turn this off (SEED_USERS=false) and create the first
 * admin through a secure, one-time process.
 */
@ConfigurationProperties(prefix = "shopeasy.seed")
public record SeedProperties(boolean enabled) {
}
