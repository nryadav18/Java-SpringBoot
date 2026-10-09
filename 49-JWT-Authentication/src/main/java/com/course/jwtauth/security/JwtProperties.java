package com.course.jwtauth.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * JwtProperties - type-safe binding of the app.jwt.* settings (topic 29).
 *
 *   app.jwt.secret=<Base64 text of at least 32 random bytes = 256 bits>
 *   app.jwt.expiration=15m
 *   app.jwt.issuer=food-delivery-api
 *
 * Duration understands "15m", "1h", "30s", "PT15M"...
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, Duration expiration, String issuer) { }
