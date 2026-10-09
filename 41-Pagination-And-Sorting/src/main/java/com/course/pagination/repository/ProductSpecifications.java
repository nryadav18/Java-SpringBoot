package com.course.pagination.repository;

import com.course.pagination.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/*
 * Reusable filters for the search endpoint (Specifications were introduced in topic 39).
 * Combined with a Pageable, they give "search + paging + sorting" in one query.
 */
public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> categoryIs(String category) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<Product> nameContains(String text) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + text.toLowerCase() + "%");
    }

    public static Specification<Product> priceAtLeast(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Product> priceAtMost(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }
}
