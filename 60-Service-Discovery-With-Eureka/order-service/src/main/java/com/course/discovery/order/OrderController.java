/*
 * OrderController.java  (Topic 60 - order-service)
 *
 * WHAT: Places orders (calling product-service by NAME) and offers two learning endpoints:
 *         GET /api/orders/discovery   -> what does Eureka know about product-service?
 *         GET /api/orders/lb-demo     -> call product-service 6 times and see which
 *                                        instance answered each time (round-robin)
 *
 * TRY:  POST http://localhost:8082/api/orders     body: {"productId":1,"quantity":2}
 *       GET  http://localhost:8082/api/orders
 *       GET  http://localhost:8082/api/orders/discovery
 *       GET  http://localhost:8082/api/orders/lb-demo
 */
package com.course.discovery.order;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ProductClient productClient;
    // DiscoveryClient = a read-only view of the Eureka registry (auto-configured for us).
    private final DiscoveryClient discoveryClient;

    private final List<Order> orders = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public OrderController(ProductClient productClient, DiscoveryClient discoveryClient) {
        this.productClient = productClient;
        this.discoveryClient = discoveryClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(@RequestBody OrderRequest request) {
        if (request.productId() == null || request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required and quantity must be at least 1");
        }
        ProductDto product = productClient.getProduct(request.productId());
        if (product.stock() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock for " + product.name());
        }
        Order order = new Order(nextId.getAndIncrement(), product.id(), product.name(),
                request.quantity(), product.price() * request.quantity(), product.servedBy());
        orders.add(order);
        return order;
    }

    @GetMapping
    public List<Order> findAll() {
        return orders;
    }

    /** Shows the raw registry data: every live instance of product-service. */
    @GetMapping("/discovery")
    public List<String> productServiceInstances() {
        List<String> result = new ArrayList<>();
        for (ServiceInstance instance : discoveryClient.getInstances("product-service")) {
            result.add(instance.getInstanceId() + " -> " + instance.getUri());
        }
        return result;   // e.g. ["product-service:8081 -> http://localhost:8081", ...]
    }

    /** Calls product-service 6 times. With 2 instances you will see 8081 and 8091 take turns. */
    @GetMapping("/lb-demo")
    public List<String> loadBalancingDemo() {
        List<String> answers = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            ProductDto p = productClient.getProduct(1L);
            answers.add("call " + i + " answered by " + p.servedBy());
        }
        return answers;
    }
}
