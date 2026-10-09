package com.course.rabbitmq.notification;

/*
 * The analytics queue receives ALL order events ("order.#"), i.e. two different
 * message types. With @RabbitListener on the CLASS and @RabbitHandler on methods,
 * Spring picks the right method based on the converted payload type
 * (it reads the "__TypeId__" header our JSON converter added).
 */
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.course.rabbitmq.order.OrderCancelledEvent;
import com.course.rabbitmq.order.OrderPlacedEvent;

@Component
@RabbitListener(queues = "${app.rabbit.analytics-queue}")
public class AnalyticsListener {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsListener.class);

    private final AtomicInteger placed = new AtomicInteger();
    private final AtomicInteger cancelled = new AtomicInteger();

    @RabbitHandler
    public void onPlaced(OrderPlacedEvent event) {
        log.info("ANALYTICS: order placed ({} so far), amount {}", placed.incrementAndGet(), event.totalAmount());
    }

    @RabbitHandler
    public void onCancelled(OrderCancelledEvent event) {
        log.info("ANALYTICS: order {} cancelled ({} so far), reason: {}",
                event.orderId(), cancelled.incrementAndGet(), event.reason());
    }
}
