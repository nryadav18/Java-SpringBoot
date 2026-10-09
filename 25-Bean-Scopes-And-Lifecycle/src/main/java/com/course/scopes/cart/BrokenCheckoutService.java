/*
 * THE TRAP: injecting a PROTOTYPE bean directly into a SINGLETON.
 *
 * This singleton is created ONCE, so its constructor runs ONCE, so it receives
 * ONE ShoppingCart - forever. Every customer ends up sharing that same cart!
 * The prototype scope is "lost" because injection happens only one time.
 */
package com.course.scopes.cart;

import org.springframework.stereotype.Service;

@Service
public class BrokenCheckoutService {

    private final ShoppingCart cart;   // injected once -> the same cart for everybody

    public BrokenCheckoutService(ShoppingCart cart) {
        this.cart = cart;
    }

    public ShoppingCart cartFor(String customer) {
        return cart;                    // BUG: ignores the customer, always the same object
    }
}
