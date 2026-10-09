/*
 * OrderRequest.java  (Topic 59 - order-service)
 *
 * WHAT: The JSON body a client sends to place an order:
 *       {"productId": 1, "quantity": 2}
 */
package com.course.msintro.order;

public record OrderRequest(Long productId, int quantity) {
}
