/*
 * Topic 65 - Distributed Tracing | order-service (the entry point of the trace)
 * ---------------------------------------------------------------
 * A user places an order -> order-service validates it -> calls payment-service.
 * One user click = ONE trace that spans BOTH services. Open Zipkin (http://localhost:9411)
 * to see the tree of spans, or simply compare the traceId in both consoles.
 *
 * Run (after payment-service):  cd order-service -> mvn spring-boot:run   (http://localhost:8081)
 */
package com.course.tracing.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
