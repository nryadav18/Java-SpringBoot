package com.course.rabbitmq.notification;

/*
 * The CONSUMER side. @RabbitListener makes Spring:
 *   - open a connection and subscribe to the queue,
 *   - convert each message's JSON body into an OrderPlacedEvent,
 *   - call this method for every message,
 *   - ACK the message when the method returns normally (RabbitMQ then deletes it),
 *   - on an exception: retry (see application.properties) and finally REJECT it,
 *     which sends it to the dead-letter exchange.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.course.rabbitmq.order.OrderPlacedEvent;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = "${app.rabbit.notification-queue}")
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order {} for {}", event.orderId(), event.customerEmail());

        // Simulate a broken email server for some addresses, to SEE retries + dead-lettering.
        if (event.customerEmail().endsWith("@fail.test")) {
            log.warn("Email server rejected {} - throwing so the message is retried", event.customerEmail());
            throw new IllegalStateException("Email server unavailable for " + event.customerEmail());
        }

        // Pretend to send an email (in Topic 54 you learned to send real ones).
        log.info("EMAIL to {}: Hi {}, your order of {} x {} (total {}) is confirmed!",
                event.customerEmail(), event.customerName(), event.quantity(), event.product(), event.totalAmount());
    }
}
