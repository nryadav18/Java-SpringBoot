package com.course.shopeasy.cart;

import java.math.BigDecimal;
import java.util.List;

/** The whole cart as JSON: the lines plus totals. */
public record CartResponse(
        List<CartItemResponse> items,
        int totalQuantity,
        BigDecimal totalAmount) {

    public static CartResponse empty() {
        return new CartResponse(List.of(), 0, BigDecimal.ZERO);
    }
}
