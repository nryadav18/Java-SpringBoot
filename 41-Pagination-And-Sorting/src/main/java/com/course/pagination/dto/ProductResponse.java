package com.course.pagination.dto;

import com.course.pagination.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/* One product in the JSON response. */
public record ProductResponse(Long id, String name, String category, BigDecimal price, int stock,
                              LocalDateTime createdAt) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getStock(),
                p.getCreatedAt());
    }
}
