/*
 * ApiGatewayApplication.java  (Topic 61 - api-gateway)
 *
 * WHAT: The entry point of the API gateway. No special annotation is needed:
 *       the gateway starter on the classpath + the routes in application.yml are enough.
 *
 *       The gateway is REACTIVE (Spring WebFlux on Netty). You don't have to write
 *       reactive code for routes (they are pure YAML), but the custom filters in this
 *       project return Mono<Void> - we explain that in the lesson.
 *
 * RUN:  Start eureka-server, product-service, order-service first, then:
 *       cd api-gateway
 *       mvn spring-boot:run          -> http://localhost:8080/api/products
 */
package com.course.gateway.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
