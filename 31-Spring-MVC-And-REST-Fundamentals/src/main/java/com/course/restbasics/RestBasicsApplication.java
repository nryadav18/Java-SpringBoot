package com.course.restbasics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 31 - Spring MVC & REST Fundamentals
 * -----------------------------------------
 * Starts an embedded Tomcat on port 8080 with a small in-memory Book API.
 *
 * How to run:  mvn spring-boot:run
 * Try:         http://localhost:8080/api/books        (in your browser)
 *              http://localhost:8080/                 (an HTML page from a @Controller)
 * More requests in requests.http and in lesson.html (curl).
 */
@SpringBootApplication
public class RestBasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestBasicsApplication.class, args);
    }
}
