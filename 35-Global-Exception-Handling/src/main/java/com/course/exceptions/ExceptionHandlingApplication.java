package com.course.exceptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 35 - Global Exception Handling
 * ------------------------------------
 * A small Library API (books) where EVERY error becomes a clean JSON response.
 *
 * The same app can run in three "error styles" (chosen with a Spring profile, topic 29):
 *
 *   (no profile)  -> GlobalExceptionHandler       -> our own ErrorResponse DTO
 *   problem       -> ProblemDetailExceptionHandler -> RFC 9457 "application/problem+json"
 *   plain         -> no handler at all            -> Spring Boot's DEFAULT error JSON
 *
 * Run:
 *   mvn spring-boot:run
 *   mvn spring-boot:run -Dspring-boot.run.profiles=problem
 *   mvn spring-boot:run -Dspring-boot.run.profiles=plain
 */
@SpringBootApplication
public class ExceptionHandlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExceptionHandlingApplication.class, args);
    }
}
