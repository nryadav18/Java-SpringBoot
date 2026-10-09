package com.course.securitybasics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 47 - Spring Security Fundamentals
 * ---------------------------------------
 * Entry point of a tiny "College Portal" API that is protected by Spring Security.
 *
 *   GET  /api/public/notice  -> anyone (no login)
 *   GET  /api/me             -> any logged-in user  (else 401 Unauthorized)
 *   GET  /api/admin/stats    -> only users with role ADMIN (USER gets 403 Forbidden)
 *   POST /api/feedback       -> any logged-in user (works with curl because CSRF is off for /api/**)
 *   GET  /                   -> HTML page; log in with the browser login form
 *
 * Users (in memory):  student / student123 (USER)   admin / admin123 (USER, ADMIN)
 *
 * Run:   mvn spring-boot:run
 * Try:   curl -i -u student:student123 http://localhost:8080/api/me
 */
@SpringBootApplication
public class SecurityBasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityBasicsApplication.class, args);
    }
}
