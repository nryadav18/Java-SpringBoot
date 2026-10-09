package com.course.shopeasy.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Body of PUT /api/v1/cart/items/{productId}: sets the NEW quantity (it does not add to the old one). */
public record UpdateCartItemRequest(
        @Schema(example = "3")
        @NotNull @Min(1) @Max(99)
        Integer quantity) {
}
