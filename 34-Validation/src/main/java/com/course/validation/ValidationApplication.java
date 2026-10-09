package com.course.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 34 - Validation
 * ---------------------
 * A small "Event Booking" API that shows how to validate input at the API boundary:
 *   POST /api/users          -> register a user  (built-in + custom + nested constraints)
 *   GET  /api/users/{id}     -> @Min on a path variable (method validation)
 *   GET  /api/users/search   -> @Size on a query parameter
 *   POST /api/bookings       -> @Future, @Positive, @Max ...
 *   POST /api/products       -> validation group OnCreate
 *   PUT  /api/products       -> validation group OnUpdate
 *
 * Run:  mvn spring-boot:run   then try the requests in requests.http
 */
@SpringBootApplication
public class ValidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValidationApplication.class, args);
    }
}
