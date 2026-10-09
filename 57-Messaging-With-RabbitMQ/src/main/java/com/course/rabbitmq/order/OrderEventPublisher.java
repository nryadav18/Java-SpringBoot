package com.course.rabbitmq.order;

/*
 * The PRODUCER side. RabbitTemplate is to RabbitMQ what JdbcTemplate is to a database:
 * a helper that hides connections and channels.
 *
 * convertAndSend(exchange, routingKey, object):
 *   1. "convert" - turns the object into a message (JSON, thanks to our MessageConverter)
 *   2. "send"    - publishes it to the EXCHANGE (never directly to a queue!)
 *                  with a ROUTING KEY that the exchange uses to pick queues.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.course.rabbitmq.config.RabbitConfig;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String broadcastExchange;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${app.rabbit.exchange}") String exchange,
                               @Value("${app.rabbit.broadcast-exchange}") String broadcastExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.broadcastExchange = broadcastExchange;
    }

    public void publishOrderPlaced(OrderPlacedEvent event) {
        log.info("Publishing OrderPlacedEvent {} to exchange '{}' with key '{}'",
                event.orderId(), exchange, RabbitConfig.ORDER_PLACED_KEY);
        rabbitTemplate.convertAndSend(exchange, RabbitConfig.ORDER_PLACED_KEY, event);
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        log.info("Publishing OrderCancelledEvent {} with key '{}'", event.orderId(), RabbitConfig.ORDER_CANCELLED_KEY);
        rabbitTemplate.convertAndSend(exchange, RabbitConfig.ORDER_CANCELLED_KEY, event);
    }

    public void broadcast(String announcement) {
        // Fanout exchange: the routing key is ignored, so we pass an empty string.
        rabbitTemplate.convertAndSend(broadcastExchange, "", announcement);
    }
}
