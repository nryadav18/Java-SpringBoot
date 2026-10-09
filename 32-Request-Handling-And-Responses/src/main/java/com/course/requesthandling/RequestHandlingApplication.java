package com.course.requesthandling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 32 - Request Handling & Responses
 * ---------------------------------------
 * A Product API with full CRUD (GET all, GET one, POST, PUT, PATCH, DELETE),
 * kept in memory.
 *
 * How to run:  mvn spring-boot:run
 * Try:         curl -i http://localhost:8080/api/products
 * All requests: requests.http (VS Code REST Client) or the curl list in lesson.html.
 */
@SpringBootApplication
public class RequestHandlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestHandlingApplication.class, args);
    }
}
