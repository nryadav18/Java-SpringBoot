/*
 * Topic 65 - Distributed Tracing | payment-service
 * ---------------------------------------------------------------
 * Receives "please charge X for order Y" requests from order-service.
 * We write ZERO tracing code for the basic case: because the incoming request carries a
 * "traceparent" header, Spring Boot automatically continues the SAME trace here.
 * You will see the same traceId in the logs of BOTH services.
 *
 * Run:  cd payment-service  ->  mvn spring-boot:run     (http://localhost:8082)
 */
package com.course.tracing.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
