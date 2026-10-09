package com.course.kafka;

/*
 * Topic 58 - Event Streaming with Apache Kafka
 * --------------------------------------------
 * Story: every change of an order (CREATED, PAID, SHIPPED, CANCELLED) is written
 * as an EVENT to the Kafka topic "orders". Two independent services read the
 * same stream, each at its own pace:
 *   - notification-service (NotificationConsumer)  -> "sends" messages to customers
 *   - analytics-service    (AnalyticsConsumer)     -> counts events per type
 * Failed records end up in the dead-letter topic "orders.DLT".
 *
 * Run:  docker compose up -d     (Kafka)
 *       mvn spring-boot:run
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaOrdersApplication.class, args);
    }
}
