package com.course.observability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 53 - Actuator, Monitoring and Observability
 * ------------------------------------------------------------
 * A tiny "Order Service" that is easy to WATCH in production:
 *
 *   /actuator/health      -> is the app OK? (includes our custom PaymentGatewayHealthIndicator)
 *   /actuator/info        -> what app is this? (build info, java, our ShopInfoContributor)
 *   /actuator/metrics     -> numbers: JVM memory, HTTP requests, and our custom shop.orders.* metrics
 *   /actuator/prometheus  -> the same metrics in Prometheus format (for Grafana dashboards)
 *   /actuator/loggers     -> view AND change log levels while the app is running
 *
 * Run:  mvn spring-boot:run                                    (safe set of endpoints)
 *       mvn spring-boot:run -Dspring-boot.run.profiles=dev     (ALL endpoints - learning only!)
 */
@SpringBootApplication
public class ObservabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilityApplication.class, args);
    }
}
