package com.course.kafka.consumer;

/*
 * CONSUMER #1 - consumer group "notification-service".
 *
 * concurrency = "3" starts 3 consumer threads in this group. Kafka gives each
 * thread its own partition(s) of the "orders" topic (3 partitions -> 1 each).
 * A 4th thread would sit idle: one partition is read by at most ONE consumer of a group.
 *
 * We receive the whole ConsumerRecord so we can print the key, partition and offset.
 */
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.course.kafka.order.OrderEvent;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "notification-service", concurrency = "3")
    public void onOrderEvent(ConsumerRecord<String, OrderEvent> record) {
        OrderEvent event = record.value();
        log.info("NOTIFY  partition={} offset={} key={} status={} customer={}",
                record.partition(), record.offset(), record.key(), event.status(), event.customerName());

        if (event.amount() != null && event.amount().signum() < 0) {
            // Bad data: retrying will never help -> configured as NOT retryable -> straight to orders.DLT
            throw new IllegalArgumentException("Negative amount in order " + event.orderId());
        }
        if ("fail".equalsIgnoreCase(event.customerName())) {
            // Simulates a temporary problem (e.g. SMS gateway down) -> retried, then orders.DLT
            throw new IllegalStateException("SMS gateway unavailable");
        }

        String text = switch (event.status()) {
            case CREATED -> "Thanks " + event.customerName() + ", we received your order!";
            case PAID -> "Payment received for order " + event.orderId();
            case SHIPPED -> "Good news! Your order is on the way.";
            case CANCELLED -> "Your order was cancelled.";
        };
        log.info("NOTIFY  message to {}: {}", event.customerName(), text);
    }
}
