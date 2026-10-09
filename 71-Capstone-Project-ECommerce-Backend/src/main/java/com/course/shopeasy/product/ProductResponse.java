package com.course.shopeasy.product;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * What clients see for a product. Flattened: categoryId + categoryName instead of a nested entity,
 * so there is no lazy-loading or infinite-recursion risk in JSON (topic 40).
 * Internal fields (version, active) are deliberately not exposed.
 */
public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        boolean inStock,
        String imageUrl,
        Long categoryId,
        String categoryName,
        Instant createdAt,
        Instant updatedAt) {
}
