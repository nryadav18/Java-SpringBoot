/*
 * THE FIX: inject an ObjectProvider<ShoppingCart> instead of a ShoppingCart.
 *
 * ObjectProvider is a small "factory handle" from Spring. Each call to getObject()
 * asks the container again - and because ShoppingCart is a prototype, the container
 * creates a NEW cart each time.
 *
 * (Other fixes you may meet: @Lookup methods, or a scoped proxy:
 *  @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS).)
 */
package com.course.scopes.cart;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CheckoutService {

    private final ObjectProvider<ShoppingCart> cartProvider;
    // One cart per customer. (A real web app would keep this in the HTTP session - see "session scope".)
    private final Map<String, ShoppingCart> cartsByCustomer = new HashMap<>();

    public CheckoutService(ObjectProvider<ShoppingCart> cartProvider) {
        this.cartProvider = cartProvider;
    }

    public ShoppingCart cartFor(String customer) {
        // computeIfAbsent: create a new cart only the first time we see this customer
        return cartsByCustomer.computeIfAbsent(customer, c -> cartProvider.getObject());
    }
}
