/*
 * PROTOTYPE scope.
 *
 * A shopping cart holds STATE that belongs to one customer. Sharing one cart between
 * all customers would be a disaster, so every request for this bean must produce
 * a brand-new object:  @Scope("prototype")  (or ConfigurableBeanFactory.SCOPE_PROTOTYPE).
 *
 * Lifecycle note: Spring creates and initialises prototypes (@PostConstruct runs),
 * then hands them over and FORGETS them. @PreDestroy is NEVER called for a prototype -
 * cleaning up is your job.
 */
package com.course.scopes.cart;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)   // same as @Scope("prototype")
public class ShoppingCart {

    private static int counter = 0;                // just to give each cart a readable id
    private final int cartNumber;
    private final List<String> items = new ArrayList<>();

    public ShoppingCart() {
        cartNumber = ++counter;
    }

    @PostConstruct
    void ready() {
        System.out.println("   [cart] new ShoppingCart #" + cartNumber + " created (@PostConstruct runs for prototypes too)");
    }

    @PreDestroy
    void cleanup() {
        // You will NOT see this line in the output: Spring doesn't destroy prototypes.
        System.out.println("   [cart] ShoppingCart #" + cartNumber + " destroyed");
    }

    public void add(String item) {
        items.add(item);
    }

    public List<String> items() {
        return List.copyOf(items);
    }

    public int cartNumber() {
        return cartNumber;
    }
}
