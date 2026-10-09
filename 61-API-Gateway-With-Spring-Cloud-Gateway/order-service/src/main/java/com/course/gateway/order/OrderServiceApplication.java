/*
 * OrderServiceApplication.java  (Topic 61 - order-service)
 *
 * WHAT: A minimal order microservice (same idea as Topic 60). It registers in Eureka as
 *       "order-service"; the gateway forwards /api/orders/** to it. It calls
 *       product-service DIRECTLY by name (service-to-service calls usually do NOT go
 *       back out through the public gateway).
 *
 * RUN:  cd order-service
 *       mvn spring-boot:run          (port 8082)
 */
package com.course.gateway.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
