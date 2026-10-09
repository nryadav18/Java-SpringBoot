package com.course.shopeasy.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** An order as JSON. */
public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        Long customerId,
        String customerEmail,
        List<OrderItemResponse> items,
        Instant createdAt,
        Instant updatedAt) {
}
