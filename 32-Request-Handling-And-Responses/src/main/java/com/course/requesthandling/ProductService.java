package com.course.requesthandling;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

/*
 * In-memory product store + the business rules for each CRUD operation.
 * ConcurrentHashMap + AtomicLong = safe when many requests arrive at the same time.
 * Methods return Optional / boolean so the CONTROLLER can decide the HTTP status (200 vs 404).
 */
@Service
public class ProductService {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public ProductService() {
        create(new ProductRequest("Wireless Mouse", "electronics", new BigDecimal("799.00"), 50));
        create(new ProductRequest("Mechanical Keyboard", "electronics", new BigDecimal("3499.00"), 20));
        create(new ProductRequest("Java Coffee Mug", "kitchen", new BigDecimal("299.00"), 100));
        create(new ProductRequest("Desk Lamp", "home", new BigDecimal("1299.00"), 0));
    }

    /** All products, optionally filtered by category and max price, sorted by the given field. */
    public List<Product> findAll(String category, BigDecimal maxPrice, String sortBy) {
        Comparator<Product> order = switch (sortBy) {
            case "name" -> Comparator.comparing(Product::getName);
            case "price" -> Comparator.comparing(Product::getPrice);
            default -> Comparator.comparing(Product::getId);
        };
        return products.values().stream()
                .filter(p -> category == null || p.getCategory().equalsIgnoreCase(category))
                .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
                .sorted(order)
                .toList();
    }

    public Optional<Product> findById(long id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product create(ProductRequest request) {
        long id = nextId.getAndIncrement();
        String supplierCode = "SUP-" + (1000 + id);   // internal detail, hidden from JSON by @JsonIgnore
        Product product = new Product(id, request.name(), request.category(), request.price(),
                request.quantityInStock(), supplierCode);
        products.put(id, product);
        return product;
    }

    /** PUT = replace ALL client-editable fields. Missing fields in the JSON become null/0. */
    public Optional<Product> replace(long id, ProductRequest request) {
        Product existing = products.get(id);
        if (existing == null) {
            return Optional.empty();
        }
        existing.setName(request.name());
        existing.setCategory(request.category());
        existing.setPrice(request.price());
        existing.setQuantityInStock(request.quantityInStock());
        existing.setUpdatedAt(LocalDateTime.now());
        return Optional.of(existing);
    }

    /** PATCH = change ONLY the fields that were sent (non-null). */
    public Optional<Product> patch(long id, ProductPatchRequest patch) {
        Product existing = products.get(id);
        if (existing == null) {
            return Optional.empty();
        }
        if (patch.name() != null) existing.setName(patch.name());
        if (patch.category() != null) existing.setCategory(patch.category());
        if (patch.price() != null) existing.setPrice(patch.price());
        if (patch.quantityInStock() != null) existing.setQuantityInStock(patch.quantityInStock());
        existing.setUpdatedAt(LocalDateTime.now());
        return Optional.of(existing);
    }

    public boolean delete(long id) {
        return products.remove(id) != null;
    }
}
