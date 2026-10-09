/*
 * ProductServiceApplication.java  (Topic 61 - product-service)
 *
 * WHAT: The product microservice (same as Topic 60). It registers in Eureka as
 *       "product-service", and the gateway forwards /api/products/** to it.
 *
 * RUN:  cd product-service
 *       mvn spring-boot:run          (port 8081)
 *       Normally you call it THROUGH the gateway: http://localhost:8080/api/products
 */
package com.course.gateway.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
