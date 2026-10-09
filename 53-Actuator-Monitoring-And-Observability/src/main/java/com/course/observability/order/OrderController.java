package com.course.observability.order;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Normal business endpoints. Every request to them is ALSO measured automatically by Spring Boot
 * in the built-in metric "http.server.requests" (tags: uri, method, status, outcome).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    public record PlaceOrderRequest(String product, BigDecimal amount, String paymentMethod) { }

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> place(@RequestBody PlaceOrderRequest request) {
        Order order = orderService.placeOrder(request.product(), request.amount(), request.paymentMethod());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    public List<Order> all() {
        return orderService.findAll();
    }
}
