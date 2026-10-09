package com.course.taskapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 44 - REST API Best Practices and OpenAPI
 *
 * A small "Task API" (think: a to-do app for a team) that follows the
 * REST rules professional teams use, and documents itself with OpenAPI.
 *
 * Run:   mvn spring-boot:run
 * Open:  http://localhost:8080/swagger-ui.html   (interactive docs)
 *        http://localhost:8080/v3/api-docs        (raw OpenAPI JSON)
 *        http://localhost:8080/api/v1/tasks       (the API itself)
 */
@SpringBootApplication
public class TaskApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskApiApplication.class, args);
    }
}
