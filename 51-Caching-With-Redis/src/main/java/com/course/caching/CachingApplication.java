package com.course.caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 51 - Caching with Redis
 * ------------------------------------------------------------
 * Entry point of the "Product Catalog" demo.
 *
 * The ProductRepository pretends to be a SLOW database (2 seconds per call).
 * The ProductService puts a cache in front of it, so the SECOND call for the
 * same product comes back in a few milliseconds.
 *
 * How to run:
 *   mvn spring-boot:run                                         -> in-memory cache (ConcurrentMapCache), no Redis needed
 *   docker compose up -d   then
 *   mvn spring-boot:run -Dspring-boot.run.profiles=redis        -> real Redis cache with TTL + JSON values
 *
 * Then call:  GET http://localhost:8080/api/products/1   (twice, and compare the X-Response-Time-Ms header)
 */
@SpringBootApplication
public class CachingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CachingApplication.class, args);
    }
}
