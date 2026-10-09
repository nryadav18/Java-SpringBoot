/*
 * ProductController.java  (Topic 59 - product-service)
 *
 * WHAT: The public "front door" (API) of the product service.
 *       Other services (like order-service) talk to product-service ONLY through these URLs.
 *       That is the golden rule of microservices: talk through APIs, never share a database.
 *
 * TRY:  GET http://localhost:8081/api/products
 *       GET http://localhost:8081/api/products/1
 *       GET http://localhost:8081/api/products/99   -> 404 Not Found
 */
package com.course.msintro.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // In a real system this would be product-service's OWN database ("database per service").
    // To keep the topic focused, we use an in-memory map (read-only here, so a TreeMap is fine
    // and keeps the products sorted by id in the JSON output).
    private final Map<Long, Product> products = new TreeMap<>(Map.of(
            1L, new Product(1L, "Laptop", 55000.0, 10),
            2L, new Product(2L, "Wireless Mouse", 799.0, 150),
            3L, new Product(3L, "Mechanical Keyboard", 3499.0, 40)
    ));

    @GetMapping
    public Collection<Product> findAll() {
        return products.values();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Product product = products.get(id);
        if (product == null) {
            // 404 tells the CALLER (maybe another service) "this product does not exist".
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}
