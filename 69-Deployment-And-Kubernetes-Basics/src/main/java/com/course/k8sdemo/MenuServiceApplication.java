package com.course.k8sdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/*
 * Topic 69 - Deployment and Kubernetes basics.
 *
 * "menu-service" of a food-delivery company. The business logic is tiny on purpose;
 * the lesson is about running it SAFELY in production:
 *   - configuration from environment variables (Kubernetes ConfigMap + Secret)
 *   - health probes   /actuator/health/liveness   and   /actuator/health/readiness
 *   - graceful shutdown (server.shutdown=graceful)
 *   - an endpoint that shows WHICH pod answered (to see load balancing)
 *
 * Run locally:  mvn spring-boot:run      ->  http://localhost:8080/api/info
 */
@SpringBootApplication
@ConfigurationPropertiesScan   // finds the AppProperties record
public class MenuServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MenuServiceApplication.class, args);
    }
}
