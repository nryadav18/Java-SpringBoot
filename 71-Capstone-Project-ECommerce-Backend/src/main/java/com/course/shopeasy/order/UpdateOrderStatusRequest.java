package com.course.shopeasy.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Body of PATCH /api/v1/admin/orders/{id}/status (ADMIN). */
public record UpdateOrderStatusRequest(
        @Schema(example = "SHIPPED")
        @NotNull
        OrderStatus status) {
}
