/*
 * ProductServiceApplication.java  (Topic 60 - product-service)
 *
 * WHAT: The product microservice. Because spring-cloud-starter-netflix-eureka-client
 *       is on the classpath, at startup it automatically says to Eureka:
 *       "Hi, I am PRODUCT-SERVICE and I live at localhost:8081". Every 5 seconds
 *       (our dev setting) it sends a "heartbeat" ("I am still alive").
 *
 *       Note: older tutorials put @EnableEurekaClient or @EnableDiscoveryClient here.
 *       That is NOT needed any more - the starter dependency is enough.
 *
 * RUN:  Start eureka-server first, then:
 *       cd product-service
 *       mvn spring-boot:run                                                  (instance 1, port 8081)
 *       mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8091" (instance 2, port 8091)
 */
package com.course.discovery.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
