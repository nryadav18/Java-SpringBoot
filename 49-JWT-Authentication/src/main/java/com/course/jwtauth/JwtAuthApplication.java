package com.course.jwtauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/*
 * Topic 49 - JWT Authentication
 * -----------------------------
 * A small "Food Delivery" API secured with JSON Web Tokens - no sessions, no cookies.
 *
 *   POST /api/auth/register      -> public: create account, returns a token
 *   POST /api/auth/login         -> public: username + password -> token
 *   GET  /api/hello              -> needs a valid token
 *   GET  /api/orders             -> needs a valid token (shows the current user's orders)
 *   GET  /api/admin/dashboard    -> needs a valid token of a user with role ADMIN
 *
 * Seeded users: admin / admin123 (ADMIN), alice / alice123 (USER)
 *
 * Run:  mvn spring-boot:run
 * Flow: 1) login  2) copy "accessToken"  3) send header  Authorization: Bearer <token>
 *
 * @ConfigurationPropertiesScan finds the JwtProperties record (app.jwt.* settings).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class JwtAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthApplication.class, args);
    }
}
