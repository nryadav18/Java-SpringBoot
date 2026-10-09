/*
 * OrderController - order-service's own REST API.
 *
 *   POST /api/orders                         body {"productId":1,"quantity":2} -> place an order
 *   GET  /api/orders                         -> all orders placed so far
 *   GET  /api/orders/products                -> product list, fetched from product-service via Feign
 *   GET  /api/orders/timeout-demo?productId=1&delayMs=3000 -> shows Feign's read-timeout
 */
package com.course.feign.order.controller;

import com.course.feign.order.dto.OrderRequest;
import com.course.feign.order.dto.OrderResponse;
import com.course.feign.order.dto.ProductResponse;
import com.course.feign.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping
    public List<OrderResponse> allOrders() {
        return orderService.findAllOrders();
    }

    @GetMapping("/products")
    public List<ProductResponse> products() {
        return orderService.availableProducts();
    }

    @GetMapping("/timeout-demo")
    public ProductResponse timeoutDemo(@RequestParam(defaultValue = "1") Long productId,
                                       @RequestParam(defaultValue = "3000") long delayMs) {
        return orderService.timeoutDemo(productId, delayMs);
    }
}
