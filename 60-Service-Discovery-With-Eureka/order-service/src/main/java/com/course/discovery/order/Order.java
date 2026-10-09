/*
 * Order.java  (Topic 60 - order-service)
 *
 * WHAT: An order. "productServedBy" records which product-service instance gave us
 *       the product details - handy to watch load balancing in action.
 */
package com.course.discovery.order;

public record Order(Long orderId,
                    Long productId,
                    String productName,
                    int quantity,
                    double totalPrice,
                    String productServedBy) {
}
