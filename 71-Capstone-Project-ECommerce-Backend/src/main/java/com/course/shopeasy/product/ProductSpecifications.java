package com.course.shopeasy.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

/**
 * Builds the WHERE clause for the product search from small, reusable pieces (topic 39: Specifications).
 *
 * GET /api/v1/products?q=phone&categoryId=1&minPrice=1000&maxPrice=30000&inStock=true
 * becomes roughly:
 * <pre>
 *   WHERE active = true
 *     AND (lower(name) LIKE '%phone%' OR lower(description) LIKE '%phone%')
 *     AND category_id = 1
 *     AND price >= 1000 AND price <= 30000
 *     AND stock_quantity > 0
 * </pre>
 * Filters the client did not send are simply left out. The values are bound as parameters, so this is
 * safe from SQL injection (topic 19).
 */
public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> from(ProductSearchCriteria criteria) {
        List<Specification<Product>> specs = new ArrayList<>();
        specs.add(isActive());   // soft-deleted products are never listed

        if (criteria.q() != null && !criteria.q().isBlank()) {
            specs.add(textContains(criteria.q().trim()));
        }
        if (criteria.categoryId() != null) {
            specs.add(inCategory(criteria.categoryId()));
        }
        if (criteria.minPrice() != null) {
            specs.add(priceAtLeast(criteria.minPrice()));
        }
        if (criteria.maxPrice() != null) {
            specs.add(priceAtMost(criteria.maxPrice()));
        }
        if (Boolean.TRUE.equals(criteria.inStock())) {
            specs.add(inStock());
        }
        return Specification.allOf(specs);
    }

    static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    static Specification<Product> textContains(String text) {
        String pattern = "%" + text.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern));
    }

    static Specification<Product> inCategory(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    static Specification<Product> priceAtLeast(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    static Specification<Product> priceAtMost(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }

    static Specification<Product> inStock() {
        return (root, query, cb) -> cb.greaterThan(root.get("stockQuantity"), 0);
    }
}
