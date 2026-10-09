package com.course.shopeasy.cart;

import java.math.BigDecimal;

/** One cart line as JSON. availableStock lets the UI warn "only 2 left" before checkout. */
public record CartItemResponse(
        Long productId,
        String sku,
        String productName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal,
        int availableStock) {
}
