/*
 * ProductDto.java  (Topic 59 - order-service)
 *
 * WHAT: order-service's OWN copy of "what a product looks like" when it arrives as JSON.
 *       Notice: we do NOT share the Product class from product-service (no shared jar!).
 *       Each service has its own small DTO. This keeps services independent:
 *       if product-service adds new fields, Jackson simply ignores the ones we don't know.
 */
package com.course.msintro.order;

public record ProductDto(Long id, String name, double price, int stock) {
}
