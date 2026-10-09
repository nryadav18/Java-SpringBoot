/*
 * ProductServiceApplication.java  (Topic 62 - product-service)
 *
 * WHAT: A Config Server CLIENT. Before the app creates its beans, Spring Boot reads
 *       spring.config.import=optional:configserver:http://localhost:8888 (application.yml)
 *       and downloads:
 *         config-repo/application.yml              (shared by all services)
 *         config-repo/product-service.yml          (only for this service)
 *         config-repo/product-service-<profile>.yml (only for the active profile)
 *
 * RUN:  Start config-server first, then:
 *       cd product-service
 *       mvn spring-boot:run                                      (default profile)
 *       mvn spring-boot:run "-Dspring-boot.run.profiles=dev"     (dev profile)
 */
package com.course.configdemo.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ShopProperties.class)   // register our type-safe settings class
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
