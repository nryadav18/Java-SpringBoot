package com.course.patterns.patterns.observer;

import com.course.patterns.demo.PatternDemo;
import java.math.BigDecimal;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * OBSERVER (behavioural pattern)
 * "When one object changes, all its subscribers are notified automatically."
 * Like a YouTube channel: the creator uploads once; every subscriber gets a notification.
 *
 * In Spring: ApplicationEventPublisher + @EventListener (and @TransactionalEventListener).
 *
 * Expected output:
 *   OrderPlacementService: order ORD-77 saved, publishing event...
 *     EmailListener    : sending confirmation to ravi@example.com
 *     InventoryListener: reserving stock for order ORD-77
 */
@Component
@Order(15)
public class ObserverDemo implements PatternDemo {

    private final OrderPlacementService orderPlacementService;

    public ObserverDemo(OrderPlacementService orderPlacementService) {
        this.orderPlacementService = orderPlacementService;
    }

    @Override
    public String title() {
        return "Observer - publish an event, many listeners react (Spring events)";
    }

    @Override
    public void run() {
        orderPlacementService.placeOrder("ORD-77", "ravi@example.com", new BigDecimal("1499.00"));
    }
}
