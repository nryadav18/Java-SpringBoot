package com.course.shopeasy.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of POST /api/v1/orders. The products come from the customer's cart on the server -
 * the client never sends prices or totals (it could cheat).
 */
public record PlaceOrderRequest(
        @Schema(example = "221B Baker Street, London NW1 6XE")
        @NotBlank @Size(max = 500)
        String shippingAddress) {
}
