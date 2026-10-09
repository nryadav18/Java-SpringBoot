/*
 * ProductDto.java  (Topic 60 - order-service)
 *
 * WHAT: order-service's own view of a product (filled from product-service's JSON).
 *       servedBy shows which product-service instance answered.
 */
package com.course.discovery.order;

public record ProductDto(Long id, String name, double price, int stock, String servedBy) {
}
