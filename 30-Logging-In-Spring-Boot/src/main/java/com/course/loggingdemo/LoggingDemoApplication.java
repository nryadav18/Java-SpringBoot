package com.course.loggingdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/*
 * Topic 30 - Logging in Spring Boot
 * ---------------------------------
 * A tiny "food delivery" app that logs properly instead of using System.out.println.
 *
 * How to run:   mvn spring-boot:run
 * Then:         curl -X POST "http://localhost:8080/orders?customer=Asha&item=Pizza&quantity=2"
 * Logs appear in the console AND in logs/food-delivery.log (see logback-spring.xml).
 */
@SpringBootApplication
public class LoggingDemoApplication {

    // One logger per class. "static final" = created once, shared by all instances.
    // The logger's NAME is the full class name: com.course.loggingdemo.LoggingDemoApplication
    private static final Logger log = LoggerFactory.getLogger(LoggingDemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LoggingDemoApplication.class, args);
    }

    @Bean
    CommandLineRunner startupMessage() {
        return args -> log.info("Food delivery app started. Try: POST /orders?customer=Asha&item=Pizza&quantity=2");
    }
}
