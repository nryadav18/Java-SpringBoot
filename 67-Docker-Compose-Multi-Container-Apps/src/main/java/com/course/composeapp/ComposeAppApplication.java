package com.course.composeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 67 - Docker Compose multi-container app.
 *
 * A tiny "Library" REST API (books) that stores data in a database.
 *   - On your laptop (mvn spring-boot:run)          -> H2 in-memory database (default profile)
 *   - Inside Docker Compose (docker compose up)     -> MySQL container (profile "docker")
 *
 * The Java code is the SAME in both cases. Only CONFIGURATION changes.
 * That is the big lesson: containers + externalized config = "runs everywhere".
 *
 * Run:  mvn spring-boot:run            (no Docker needed)
 *       docker compose up -d --build   (app + MySQL in containers)
 */
@SpringBootApplication
public class ComposeAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComposeAppApplication.class, args);
    }
}
