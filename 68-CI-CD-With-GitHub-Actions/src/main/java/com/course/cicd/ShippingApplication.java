package com.course.cicd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 68 - CI/CD with GitHub Actions.
 *
 * The application is deliberately SMALL: a shipping-cost calculator for an online shop.
 * The real star of this topic is .github/workflows/ci.yml, which on every push:
 *   1. checks out the code,  2. installs Java 17,  3. runs "mvn -B verify" (compile + tests),
 *   4. builds a Docker image and pushes it to Docker Hub (only on the main branch).
 *
 * Run locally:  mvn spring-boot:run
 *               then open http://localhost:8080/api/shipping/quote?weightKg=2.5&express=true
 */
@SpringBootApplication
public class ShippingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShippingApplication.class, args);
    }
}
