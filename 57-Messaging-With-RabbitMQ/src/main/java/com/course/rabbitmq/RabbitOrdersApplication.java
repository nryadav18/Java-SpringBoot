package com.course.rabbitmq;

/*
 * Topic 57 - Messaging with RabbitMQ
 * ----------------------------------
 * Story: a customer places an order (REST call). The order service does NOT
 * send the email itself. It drops an "OrderPlacedEvent" message into RabbitMQ
 * and answers the customer immediately. Listeners pick the message up later:
 *   - NotificationListener  -> "sends" a confirmation email (log line)
 *   - AnalyticsListener     -> counts orders and cancellations
 *   - DeadLetterListener    -> receives messages that failed too many times
 *
 * Run:  docker compose up -d     (RabbitMQ)
 *       mvn spring-boot:run
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitOrdersApplication.class, args);
    }
}
