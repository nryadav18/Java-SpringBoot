/*
 * Order.java  (Topic 59 - order-service)
 *
 * WHAT: The data this service owns. Notice that we COPY productName and unitPrice
 *       into the order. Why? The price may change tomorrow in product-service,
 *       but this order must remember the price the customer actually paid.
 *       Copying the little bit of data you need is normal in microservices.
 */
package com.course.msintro.order;

public record Order(Long orderId,
                    Long productId,
                    String productName,
                    int quantity,
                    double unitPrice,
                    double totalPrice,
                    String status) {
}
