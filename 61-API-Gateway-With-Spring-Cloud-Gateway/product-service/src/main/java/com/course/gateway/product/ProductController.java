/*
 * ProductController.java  (Topic 61 - product-service)
 *
 * WHAT: The product API plus one learning endpoint, /api/products/headers, which shows
 *       the headers the GATEWAY added (X-Request-Source and X-Correlation-Id).
 *       Call it directly (port 8081) and through the gateway (port 8080) and compare!
 *
 * TRY:  GET http://localhost:8080/api/products            (through the gateway)
 *       GET http://localhost:8080/api/products/headers    (through the gateway)
 *       GET http://localhost:8081/api/products/headers    (directly - no gateway headers)
 */
package com.course.gateway.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

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

    // Note: "/headers" is declared as its own mapping, so Spring prefers it over "/{id}".
    @GetMapping("/headers")
    public Map<String, String> showGatewayHeaders(
            @RequestHeader(value = "X-Request-Source", defaultValue = "(none - you called me directly)") String source,
            @RequestHeader(value = "X-Correlation-Id", defaultValue = "(none)") String correlationId) {
        Map<String, String> result = new LinkedHashMap<>();   // keeps insertion order in the JSON
        result.put("servedBy", instanceName);
        result.put("X-Request-Source", source);      // added by the AddRequestHeader route filter
        result.put("X-Correlation-Id", correlationId); // added by our LoggingGlobalFilter
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return catalog().stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
