/*
 * OrderController.java  (Topic 59 - order-service)
 *
 * WHAT: Places orders. Each order needs product details, so we call product-service
 *       through ProductClient (a SYNCHRONOUS REST call: we wait for the answer).
 *
 * TRY:  POST http://localhost:8082/api/orders   body: {"productId":1,"quantity":2}
 *       GET  http://localhost:8082/api/orders
 */
package com.course.msintro.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ProductClient productClient;

    // order-service's OWN storage (in memory to keep the demo small).
    // CopyOnWriteArrayList and AtomicLong are safe when many requests (threads) arrive together.
    private final List<Order> orders = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public OrderController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(@RequestBody OrderRequest request) {
        if (request.productId() == null || request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId is required and quantity must be at least 1");
        }

        // 1) Ask product-service: "tell me about product X"  (a NETWORK call - it can be slow or fail!)
        ProductDto product = productClient.getProduct(request.productId());

        // 2) A business rule: do we have enough stock?
        if (product.stock() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only " + product.stock() + " units of " + product.name() + " left");
        }

        // 3) Save the order in OUR OWN storage.
        Order order = new Order(nextId.getAndIncrement(), product.id(), product.name(),
                request.quantity(), product.price(), product.price() * request.quantity(), "CREATED");
        orders.add(order);
        return order;
    }

    @GetMapping
    public List<Order> findAll() {
        return orders;
    }
}
