/*
 * OrderRequest - JSON body for POST /api/orders, e.g. {"productId":1,"quantity":2}
 * Validated with Bean Validation (topic 34) BEFORE we even call product-service.
 */
package com.course.feign.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull Long productId,
        @Min(1) int quantity) {
}
