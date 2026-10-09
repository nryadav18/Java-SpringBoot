package com.course.patterns.patterns.observer;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * OrderListeners.java - two OBSERVERS (subscribers) of OrderPlacedEvent.
 * Spring calls every @EventListener method whose parameter type matches the event.
 * (Normally each listener lives in its own feature package: email/, inventory/ ...)
 */
public class OrderListeners {

    private OrderListeners() {
    }

    @Component
    public static class EmailListener {
        @EventListener
        @Order(1)
        public void onOrderPlaced(OrderPlacedEvent event) {
            System.out.println("  EmailListener    : sending confirmation to " + event.customerEmail());
        }
    }

    @Component
    public static class InventoryListener {
        @EventListener
        @Order(2)
        public void onOrderPlaced(OrderPlacedEvent event) {
            System.out.println("  InventoryListener: reserving stock for order " + event.orderId());
        }
    }
}
