/*
 * Topic 25 - Bean scopes and lifecycle (online shop)
 *
 * Demos (in order):
 *   1. Container start-up: watch the numbered lifecycle steps of DatabaseConnectionPool
 *   2. Singleton vs prototype - compare identity hash codes
 *   3. Prototype inside a singleton: the trap (shared cart) and the fix (ObjectProvider)
 *   4. @Lazy - a singleton created on first use
 *   5. Closing the container - destroy callbacks (and why prototypes get none)
 *
 * How to run (from the topic folder):
 *     mvn -q compile exec:java
 */
package com.course.scopes;

import com.course.scopes.cart.BrokenCheckoutService;
import com.course.scopes.cart.CheckoutService;
import com.course.scopes.cart.ShoppingCart;
import com.course.scopes.catalog.ProductCatalog;
import com.course.scopes.config.AppConfig;
import com.course.scopes.lifecycle.DatabaseConnectionPool;
import com.course.scopes.report.SalesReportService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) {
        System.out.println("=== DEMO 1: Starting the container - lifecycle of DatabaseConnectionPool ===");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("   --- container READY ---");
        DatabaseConnectionPool pool = context.getBean(DatabaseConnectionPool.class);
        System.out.println("   using the bean: " + pool.query("SELECT * FROM products"));
        System.out.println();

        System.out.println("=== DEMO 2: Singleton vs prototype ===");
        ProductCatalog catalog1 = context.getBean(ProductCatalog.class);
        ProductCatalog catalog2 = context.getBean(ProductCatalog.class);
        // System.identityHashCode = a number based on the object's identity (Topic 6).
        System.out.println("   ProductCatalog (singleton) hash codes: "
                + System.identityHashCode(catalog1) + " / " + System.identityHashCode(catalog2)
                + " -> same object? " + (catalog1 == catalog2));
        ShoppingCart cart1 = context.getBean(ShoppingCart.class);
        ShoppingCart cart2 = context.getBean(ShoppingCart.class);
        System.out.println("   ShoppingCart (prototype) hash codes:   "
                + System.identityHashCode(cart1) + " / " + System.identityHashCode(cart2)
                + " -> same object? " + (cart1 == cart2));
        System.out.println("   (hash code numbers differ on every run - compare them, don't memorise them)");
        System.out.println();

        System.out.println("=== DEMO 3: Prototype inside a singleton ===");
        BrokenCheckoutService broken = context.getBean(BrokenCheckoutService.class);
        broken.cartFor("alice").add("Keyboard");
        broken.cartFor("bob").add("Monitor");
        System.out.println("   BROKEN: alice's cart #" + broken.cartFor("alice").cartNumber()
                + " = " + broken.cartFor("alice").items() + "   <- bob's Monitor is in alice's cart!");

        CheckoutService checkout = context.getBean(CheckoutService.class);
        checkout.cartFor("alice").add("Keyboard");
        checkout.cartFor("bob").add("Monitor");
        checkout.cartFor("alice").add("Mouse");
        System.out.println("   FIXED:  alice's cart #" + checkout.cartFor("alice").cartNumber()
                + " = " + checkout.cartFor("alice").items());
        System.out.println("   FIXED:  bob's cart #" + checkout.cartFor("bob").cartNumber()
                + "   = " + checkout.cartFor("bob").items());
        double total = checkout.cartFor("alice").items().stream().mapToDouble(catalog1::priceOf).sum();
        System.out.println("   alice pays " + total);
        System.out.println();

        System.out.println("=== DEMO 4: @Lazy ===");
        System.out.println("   Is SalesReportService created yet? It printed nothing at startup...");
        SalesReportService reports = context.getBean(SalesReportService.class);   // created HERE
        System.out.println("   " + reports.dailyReport());
        System.out.println();

        System.out.println("=== DEMO 5: Closing the container ===");
        context.close();   // destroys singletons; prototypes (the carts) get NO @PreDestroy call
        System.out.println("   (no 'ShoppingCart destroyed' lines: Spring does not manage prototype destruction)");
        System.out.println("=== Done ===");
    }
}
