/*
 * ProductController - the REST API that order-service calls through Feign.
 *
 * Endpoints:
 *   GET  /api/products                      -> all products
 *   GET  /api/products/{id}                 -> one product (404 if unknown)
 *   GET  /api/products/{id}?delayMs=3000    -> same, but answers SLOWLY (to demo Feign timeouts)
 *   POST /api/products/{id}/reduce-stock?quantity=2 -> lowers stock (409 if not enough)
 *
 * Data lives in a ConcurrentHashMap (thread-safe map) - no database needed for this topic.
 */
package com.course.feign.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    // The port is printed in every answer's log line so that, when you run TWO copies
    // (load-balancing demo with Eureka), you can see which instance answered.
    @Value("${server.port}")
    private String port;

    public ProductController() {
        products.put(1L, new Product(1L, "Wireless Mouse", 799.0, 25));
        products.put(2L, new Product(2L, "Mechanical Keyboard", 3499.0, 10));
        products.put(3L, new Product(3L, "USB-C Hub", 1299.0, 0));   // out of stock on purpose
    }

    @GetMapping
    public List<Product> findAll() {
        log.info("[port {}] GET all products", port);
        return products.values().stream()
                .sorted(Comparator.comparing(Product::id))
                .toList();
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id,
                            @RequestParam(defaultValue = "0") long delayMs) throws InterruptedException {
        log.info("[port {}] GET product {} (delayMs={})", port, id, delayMs);
        if (delayMs > 0) {
            Thread.sleep(delayMs);   // pretend the database is slow today
        }
        return findOrThrow(id);
    }

    @PostMapping("/{id}/reduce-stock")
    public Product reduceStock(@PathVariable Long id, @RequestParam int quantity) {
        Product current = findOrThrow(id);
        if (current.stock() < quantity) {
            // 409 CONFLICT - the request is valid but clashes with the current state
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only " + current.stock() + " left of product " + id);
        }
        Product updated = current.withStock(current.stock() - quantity);
        products.put(id, updated);
        log.info("[port {}] Stock of product {} reduced by {} -> {}", port, id, quantity, updated.stock());
        return updated;
    }

    private Product findOrThrow(Long id) {
        Product p = products.get(id);
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found");
        }
        return p;
    }
}
