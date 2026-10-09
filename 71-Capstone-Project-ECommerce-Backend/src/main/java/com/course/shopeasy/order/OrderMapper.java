package com.course.shopeasy.order;

import java.util.List;

/** Entity -> DTO conversion for orders (topic 33). Call it inside a transaction (lazy associations). */
public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProduct().getId(), i.getProductName(),
                        i.getUnitPrice(), i.getQuantity(), i.getLineTotal()))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}
