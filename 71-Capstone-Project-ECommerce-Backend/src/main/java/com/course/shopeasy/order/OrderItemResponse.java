package com.course.shopeasy.order;

import java.math.BigDecimal;

/** One order line as JSON. */
public record OrderItemResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal) {
}
