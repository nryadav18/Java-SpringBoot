/*
 * OrderServiceApplication.java  (Topic 60 - order-service)
 *
 * WHAT: The order microservice. Compared with Topic 59 there is ONE big change:
 *       it no longer knows "localhost:8081". It only knows the NAME "product-service"
 *       and asks Eureka where that service lives (and picks one copy if there are many).
 *
 * RUN:  Start eureka-server, then product-service (1 or 2 copies), then:
 *       cd order-service
 *       mvn spring-boot:run          -> http://localhost:8082/api/orders
 */
package com.course.discovery.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
