/*
 * Topic 63 - OpenFeign | product-service
 * ---------------------------------------------------------------
 * The "provider" side. It owns the product catalogue and exposes a plain REST API.
 * It knows NOTHING about Feign - Feign is only used by the CALLER (order-service).
 *
 * Run:  cd product-service  ->  mvn spring-boot:run   (http://localhost:8081)
 */
package com.course.feign.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
