/*
 * Product.java  (Topic 59 - product-service)
 *
 * WHAT: The data this service owns. A Java "record" is a short way to write an
 *       immutable data class (fields + constructor + getters + equals/hashCode/toString).
 *       Spring (Jackson) turns it into JSON automatically:
 *       {"id":1,"name":"Laptop","price":55000.0,"stock":10}
 */
package com.course.msintro.product;

public record Product(Long id, String name, double price, int stock) {
}
