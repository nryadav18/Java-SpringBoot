/*
 * Product.java  (Topic 60 - product-service)
 *
 * WHAT: The product data. The extra field "servedBy" tells you WHICH copy (instance)
 *       of product-service answered, e.g. "product-service:8081" or "product-service:8091".
 *       This lets you SEE load balancing working.
 */
package com.course.discovery.product;

public record Product(Long id, String name, double price, int stock, String servedBy) {
}
