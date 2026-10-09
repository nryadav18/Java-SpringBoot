/*
 * Product.java  (Topic 61 - product-service)
 *
 * WHAT: Product data + "servedBy" (which instance answered).
 */
package com.course.gateway.product;

public record Product(Long id, String name, double price, int stock, String servedBy) {
}
