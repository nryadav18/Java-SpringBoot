package com.course.caching.product;

import java.math.BigDecimal;

/*
 * A product in our online shop.
 *
 * It is a Java record (topic 15): immutable, with constructor, accessors (id(), name() ...),
 * equals/hashCode and toString generated for us.
 *
 * Immutable objects are PERFECT for caching: nobody can change a cached object
 * "behind the cache's back".
 */
public record Product(Long id, String name, String category, BigDecimal price) {

    /** Returns a copy with a new price (records cannot be changed, so we create a new one). */
    public Product withPrice(BigDecimal newPrice) {
        return new Product(id, name, category, newPrice);
    }
}
