package com.course.rabbitmq.order;

/*
 * REST entry point. Notice the response status: 202 ACCEPTED.
 * It means "I received your request and will process it" - the email etc.
 * happen LATER, asynchronously, in the listeners.
 *
 * Try:  POST http://localhost:8080/api/orders   (see requests.http)
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
@RequestMapping("/api")
public class OrderController {

    private final OrderEventPublisher publisher;

    public OrderController(OrderEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> placeOrder(@Valid @RequestBody OrderRequest request) {
        // In a real shop we would save the order in the database first (Topic 38).
        String orderId = UUID.randomUUID().toString();
        BigDecimal total = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));

        OrderPlacedEvent event = new OrderPlacedEvent(orderId, request.customerName(), request.customerEmail(),
                request.product(), request.quantity(), total, Instant.now());
        publisher.publishOrderPlaced(event);

        return Map.of("orderId", orderId, "status", "ACCEPTED",
                "message", "Order received. A confirmation email will be sent shortly.");
    }

    @PostMapping("/orders/{orderId}/cancel")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> cancelOrder(@PathVariable String orderId,
                                           @RequestParam(defaultValue = "changed my mind") String reason) {
        publisher.publishOrderCancelled(new OrderCancelledEvent(orderId, reason, Instant.now()));
        return Map.of("orderId", orderId, "status", "CANCELLATION_REQUESTED");
    }

    @PostMapping("/announcements")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> announce(@RequestBody String text) {
        publisher.broadcast(text);
        return Map.of("status", "BROADCAST_SENT");
    }
}
