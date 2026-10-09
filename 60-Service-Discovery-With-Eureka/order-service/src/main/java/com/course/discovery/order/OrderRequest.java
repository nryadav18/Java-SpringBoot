/*
 * OrderRequest.java  (Topic 60 - order-service)
 *
 * WHAT: JSON body to place an order:  {"productId": 1, "quantity": 2}
 */
package com.course.discovery.order;

public record OrderRequest(Long productId, int quantity) {
}
