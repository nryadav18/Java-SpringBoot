package com.course.shopeasy.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Body of POST /api/v1/cart/items. Max 99 per line protects against typos like 1000 instead of 10. */
public record AddCartItemRequest(
        @Schema(example = "1")
        @NotNull
        Long productId,

        @Schema(example = "2")
        @NotNull @Min(1) @Max(99)
        Integer quantity) {
}
