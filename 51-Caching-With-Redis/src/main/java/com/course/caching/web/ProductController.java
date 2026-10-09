package com.course.caching.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.course.caching.product.Product;
import com.course.caching.product.ProductService;

/*
 * REST endpoints for the product catalog.
 *
 * Every read endpoint adds a response header  X-Response-Time-Ms  so you can SEE the difference:
 *   1st call  GET /api/products/1   ->  X-Response-Time-Ms: ~2005   (cache MISS, slow database)
 *   2nd call  GET /api/products/1   ->  X-Response-Time-Ms: 0..3    (cache HIT)
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    /** Request body for creating a product (a record used as a DTO). */
    public record NewProductRequest(String name, String category, BigDecimal price) { }

    /** Request body for changing the price. */
    public record PriceUpdateRequest(BigDecimal price) { }

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        return timed(() -> productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<Product>> search(@RequestParam String category) {
        return timed(() -> productService.searchByCategory(category));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody NewProductRequest request) {
        Product created = productService.createProduct(request.name(), request.category(), request.price());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/price")
    public Product updatePrice(@PathVariable Long id, @RequestBody PriceUpdateRequest request) {
        return productService.updatePrice(id, request.price());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /** How many times did we REALLY hit the slow database? Cache hits do not count. */
    @GetMapping("/stats")
    public Map<String, Integer> stats() {
        return Map.of("databaseHits", productService.databaseHits());
    }

    /** Runs the call, measures how long it took and returns it in a header. */
    private <T> ResponseEntity<T> timed(Supplier<T> call) {
        long start = System.nanoTime();
        T body = call.get();
        long tookMs = (System.nanoTime() - start) / 1_000_000;
        return ResponseEntity.ok()
                .header("X-Response-Time-Ms", String.valueOf(tookMs))
                .body(body);
    }
}
