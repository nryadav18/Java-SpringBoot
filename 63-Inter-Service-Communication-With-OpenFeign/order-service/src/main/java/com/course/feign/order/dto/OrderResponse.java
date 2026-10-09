/*
 * OrderResponse - what order-service returns to ITS callers.
 * Notice it mixes our own data (orderId, status) with data fetched via Feign
 * (productName, price, stockLeft).
 */
package com.course.feign.order.dto;

public record OrderResponse(
        Long orderId,
        Long productId,
        String productName,
        int quantity,
        double totalPrice,
        int stockLeft,
        String status) {
}
