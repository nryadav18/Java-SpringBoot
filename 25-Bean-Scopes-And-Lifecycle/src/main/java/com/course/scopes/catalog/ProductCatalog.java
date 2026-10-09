/*
 * SINGLETON scope (the default).
 *
 * One ProductCatalog for the whole application - every customer sees the same
 * products, so sharing one object is perfect (and saves memory).
 *
 * Singletons are shared by ALL threads/users, so they must be STATELESS or
 * read-only: never keep per-customer data in a singleton's fields.
 */
package com.course.scopes.catalog;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component   // no @Scope -> singleton
public class ProductCatalog {

    private final Map<String, Double> prices = Map.of(
            "Keyboard", 1499.0,
            "Mouse", 599.0,
            "Monitor", 8999.0);

    public double priceOf(String product) {
        Double price = prices.get(product);
        if (price == null) {
            throw new IllegalArgumentException("Unknown product: " + product);
        }
        return price;
    }
}
