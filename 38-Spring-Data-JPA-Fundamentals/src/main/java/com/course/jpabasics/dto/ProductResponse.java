package com.course.jpabasics.dto;

import com.course.jpabasics.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * ProductResponse - what we send BACK to the client (JSON).
 * ---------------------------------------------------------
 * We decide exactly which fields leave the server. If we later add a secret column
 * to the entity (e.g. supplierCost), it will not leak, because it is not listed here.
 */
public record ProductResponse(
        Long id,
        String name,
        String description,
        String sku,
        BigDecimal price,
        int stock,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    /** Small static "mapper" method: entity -> DTO. (A separate mapper class also works - see topic 33.) */
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getSku(),
                p.getPrice(), p.getStock(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
