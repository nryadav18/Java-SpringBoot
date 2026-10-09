/*
 * ProductServiceApplication.java  (Topic 59 - product-service)
 *
 * WHAT: The entry point of the PRODUCT microservice.
 *       This service is the ONLY owner of product data (name, price, stock).
 *       Other services must ASK it over HTTP - they never read its data directly.
 *
 * RUN:  cd product-service
 *       mvn spring-boot:run
 *       -> http://localhost:8081/api/products
 */
package com.course.msintro.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
