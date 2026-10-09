package com.course.kafka.order;

/*
 * REST endpoints that produce order events.
 *
 *   POST /api/orders                              -> CREATED event (returns the new orderId)
 *   POST /api/orders/{orderId}/status/{status}    -> PAID / SHIPPED / CANCELLED event
 *
 * 202 ACCEPTED = "the event was handed to Kafka; consumers will react later".
 */
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderEventProducer producer;

    public OrderController(OrderEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> create(@Valid @RequestBody OrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        producer.publish(newEvent(orderId, request.customerName(), OrderStatus.CREATED, request.amount()));
        return Map.of("orderId", orderId, "status", OrderStatus.CREATED.name());
    }

    @PostMapping("/{orderId}/status/{status}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> changeStatus(@PathVariable String orderId,
                                            @PathVariable OrderStatus status,
                                            @RequestParam(defaultValue = "customer") String customerName) {
        producer.publish(newEvent(orderId, customerName, status, BigDecimal.ZERO));
        return Map.of("orderId", orderId, "status", status.name());
    }

    private OrderEvent newEvent(String orderId, String customerName, OrderStatus status, BigDecimal amount) {
        return new OrderEvent(UUID.randomUUID().toString(), orderId, customerName, status, amount, Instant.now());
    }
}
