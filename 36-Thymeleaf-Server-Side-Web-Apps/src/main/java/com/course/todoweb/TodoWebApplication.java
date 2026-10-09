package com.course.todoweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 36 - Thymeleaf: Server-Side Web Apps
 * -------------------------------------------
 * A "Todo list" web app. The SERVER builds complete HTML pages
 * (no JavaScript framework, no JSON API).
 *
 * Run:  mvn spring-boot:run      then open http://localhost:8080 in a browser.
 */
@SpringBootApplication
public class TodoWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoWebApplication.class, args);
    }
}
