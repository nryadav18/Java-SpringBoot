/*
 * Topic 64 - Resilience4j | store-service (the caller that must NOT fall over)
 * ---------------------------------------------------------------
 * The online store shows stock levels that come from inventory-service.
 * When inventory-service is down or slow, the store must still answer quickly
 * (with a sensible fallback) instead of hanging or crashing -> resilience.
 *
 * Run:  cd store-service  ->  mvn spring-boot:run     (http://localhost:8084)
 * Try it even WITHOUT inventory-service running: you will get fallback answers.
 */
package com.course.resilience.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreServiceApplication.class, args);
    }
}
