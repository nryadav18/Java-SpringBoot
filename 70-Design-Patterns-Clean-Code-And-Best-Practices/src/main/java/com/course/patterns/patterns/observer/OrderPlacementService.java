package com.course.patterns.patterns.observer;

import java.math.BigDecimal;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/*
 * OrderPlacementService.java - the SUBJECT (publisher) of the Observer pattern.
 *
 * It only announces "an order was placed". It does NOT know who listens:
 * email, inventory, analytics... Adding a new reaction = adding a new @EventListener class.
 * The publisher and the listeners are LOOSELY COUPLED. (Topic 52 shows @Async listeners too.)
 */
@Service
public class OrderPlacementService {

    private final ApplicationEventPublisher publisher;

    public OrderPlacementService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void placeOrder(String orderId, String customerEmail, BigDecimal total) {
        System.out.println("OrderPlacementService: order " + orderId + " saved, publishing event...");
        publisher.publishEvent(new OrderPlacedEvent(orderId, customerEmail, total));
    }
}
