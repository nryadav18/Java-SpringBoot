/*
 * Product - a simple immutable data carrier (a Java record, see topic 15).
 * Jackson turns it into JSON like:
 *   {"id":1,"name":"Wireless Mouse","price":799.0,"stock":25}
 */
package com.course.feign.product;

public record Product(Long id, String name, double price, int stock) {

    /** Returns a NEW product with less stock (records are immutable, so we copy). */
    public Product withStock(int newStock) {
        return new Product(id, name, price, newStock);
    }
}
