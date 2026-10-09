package com.course.layered;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 33 - Layered Architecture & DTOs
 * ---------------------------------------
 * Entry point of the "Employee Management" API.
 *
 * Because this class lives in package "com.course.layered", Spring scans
 * this package AND all sub-packages (controller, service, repository, ...).
 * That is why every layer lives in a sub-package below this one.
 *
 * Run:  mvn spring-boot:run      then open http://localhost:8080/api/employees
 */
@SpringBootApplication
public class LayeredApplication {

    public static void main(String[] args) {
        SpringApplication.run(LayeredApplication.class, args);
    }
}
