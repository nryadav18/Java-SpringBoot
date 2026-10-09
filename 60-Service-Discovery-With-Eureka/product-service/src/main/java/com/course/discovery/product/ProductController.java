/*
 * ProductController.java  (Topic 60 - product-service)
 *
 * WHAT: The product API. Every response says which instance (port) served it.
 *
 * TRY:  GET http://localhost:8081/api/products
 *       GET http://localhost:8081/api/products/1
 *       GET http://localhost:8091/api/products/1     (if you started the 2nd instance)
 */
package com.course.discovery.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Which port am I running on? (8081 for instance 1, 8091 for instance 2)
    // ${server.port} is read from application.yml or from the command line.
    private final String instanceName;

    public ProductController(@Value("${spring.application.name}") String appName,
                             @Value("${server.port}") String port) {
        this.instanceName = appName + ":" + port;
    }

    private List<Product> catalog() {
        return List.of(
                new Product(1L, "Laptop", 55000.0, 10, instanceName),
                new Product(2L, "Wireless Mouse", 799.0, 150, instanceName),
                new Product(3L, "Mechanical Keyboard", 3499.0, 40, instanceName));
    }

    @GetMapping
    public List<Product> findAll() {
        return catalog();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return catalog().stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)                          // found  -> 200 + JSON
                .orElse(ResponseEntity.notFound().build());       // missing -> 404
    }
}
