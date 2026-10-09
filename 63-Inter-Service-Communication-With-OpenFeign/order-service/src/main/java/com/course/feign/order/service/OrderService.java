/*
 * OrderService - business logic for placing orders.
 * ---------------------------------------------------------------
 * Look how CLEAN the remote calls are: productClient.getProductById(id) reads like a call
 * to a local repository, but it is really an HTTP request to another process.
 * (That is also the danger: never forget a network call can be slow or fail!)
 *
 * Orders are kept in memory (a ConcurrentHashMap) to keep the focus on Feign.
 */
package com.course.feign.order.service;

import com.course.feign.order.client.ProductClient;
import com.course.feign.order.dto.OrderRequest;
import com.course.feign.order.dto.OrderResponse;
import com.course.feign.order.dto.ProductResponse;
import com.course.feign.order.exception.OutOfStockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final ProductClient productClient;              // injected Feign proxy
    private final Map<Long, OrderResponse> orders = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1000);

    // Constructor injection (topic 24) - Spring passes in the proxy Feign generated.
    public OrderService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public OrderResponse placeOrder(OrderRequest request) {
        // 1) Remote call #1: fetch the product (404 -> ProductNotFoundException via ErrorDecoder)
        ProductResponse product = productClient.getProductById(request.productId());
        log.info("Fetched product via Feign: {}", product);

        // 2) Quick local check so we do not even try when stock is clearly too low
        if (product.stock() < request.quantity()) {
            throw new OutOfStockException("Only " + product.stock() + " left of '" + product.name() + "'");
        }

        // 3) Remote call #2: ask product-service to reduce stock (409 -> OutOfStockException)
        ProductResponse updated = productClient.reduceStock(product.id(), request.quantity());

        // 4) Save our own order
        long orderId = idSequence.incrementAndGet();
        OrderResponse order = new OrderResponse(
                orderId,
                product.id(),
                product.name(),
                request.quantity(),
                product.price() * request.quantity(),
                updated.stock(),
                "CONFIRMED");
        orders.put(orderId, order);
        log.info("Order {} placed: {} x {}", orderId, request.quantity(), product.name());
        return order;
    }

    public List<OrderResponse> findAllOrders() {
        return orders.values().stream()
                .sorted(Comparator.comparing(OrderResponse::orderId))
                .toList();
    }

    /** Pass-through so you can see Feign returning a List<ProductResponse>. */
    public List<ProductResponse> availableProducts() {
        return productClient.getAllProducts();
    }

    /** Asks product-service to answer slowly - with read-timeout 2s, 3000 ms will FAIL. */
    public ProductResponse timeoutDemo(Long productId, long delayMs) {
        return productClient.getProductSlowly(productId, delayMs);
    }
}
