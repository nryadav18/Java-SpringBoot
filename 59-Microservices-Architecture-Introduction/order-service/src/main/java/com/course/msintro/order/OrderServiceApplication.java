/*
 * OrderServiceApplication.java  (Topic 59 - order-service)
 *
 * WHAT: The entry point of the ORDER microservice.
 *       To place an order it must know the product's name and price - but that data
 *       belongs to product-service. So order-service CALLS product-service over HTTP.
 *
 * RUN:  Start product-service first (port 8081), then:
 *       cd order-service
 *       mvn spring-boot:run
 *       -> http://localhost:8082/api/orders
 */
package com.course.msintro.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
