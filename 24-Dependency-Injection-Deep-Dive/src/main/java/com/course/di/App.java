/*
 * Topic 24 - Dependency Injection deep dive  ("QuickBite" food delivery)
 *
 * Demos (in order):
 *   1. Constructor vs setter vs field injection
 *   2. Several implementations of one interface -> @Primary and @Qualifier
 *   3. Injecting a List and a Map of all beans of a type
 *   4. @Value + @PropertySource (settings from application.properties)
 *   5. Optional dependencies (Optional, ObjectProvider, required=false)
 *   6. Circular dependency: the error, and the fix by redesign
 *
 * How to run (from the topic folder):
 *     mvn -q compile exec:java
 */
package com.course.di;

import com.course.circular.fixed.FixedInventoryService;
import com.course.circular.fixed.FixedPricingService;
import com.course.di.config.AppConfig;
import com.course.di.config.ShopSettings;
import com.course.di.notify.OrderNotifier;
import com.course.di.order.CheckoutService;
import com.course.di.order.ExpressCheckoutService;
import com.course.di.order.LegacyAuditService;
import com.course.di.order.ReceiptService;
import com.course.di.payment.PaymentRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    public static void main(String[] args) {
        System.out.println("=== Starting the QuickBite container ===");
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            System.out.println();

            System.out.println("=== DEMO 1: Three styles of injection ===");
            CheckoutService checkout = context.getBean(CheckoutService.class);
            ReceiptService receipts = context.getBean(ReceiptService.class);
            LegacyAuditService audit = context.getBean(LegacyAuditService.class);
            System.out.println("   constructor -> " + checkout.checkout(250));
            System.out.println("   setter      -> " + receipts.receiptFor("Order #101, 2 x Biryani"));
            System.out.println("   field       -> " + audit.audit("order #101 paid"));
            System.out.println();

            System.out.println("=== DEMO 2: Many implementations -> @Primary and @Qualifier ===");
            System.out.println("   CheckoutService (no qualifier) uses: " + checkout.gatewayInUse() + "   <- @Primary");
            ExpressCheckoutService express = context.getBean(ExpressCheckoutService.class);
            System.out.println("   ExpressCheckoutService uses:        " + express.gatewayInUse() + "   <- @Qualifier(\"upiPayment\")");
            System.out.println("   express pay -> " + express.quickPay(99));
            System.out.println();

            System.out.println("=== DEMO 3: Inject ALL implementations (List and Map) ===");
            PaymentRegistry registry = context.getBean(PaymentRegistry.class);
            System.out.println("   List (sorted by @Order): " + registry.availableMethods());
            System.out.println("   Map keys (bean names):   " + registry.beanNames());
            System.out.println("   payWith(\"paypal\") -> " + registry.payWith("paypal", 560));
            System.out.println();

            System.out.println("=== DEMO 4: @Value + @PropertySource ===");
            ShopSettings settings = context.getBean(ShopSettings.class);
            System.out.println("   " + settings.describe());
            System.out.println("   delivery fee for 250 -> " + settings.deliveryFeeFor(250));
            System.out.println("   delivery fee for 350 -> " + settings.deliveryFeeFor(350) + " (free above 300)");
            System.out.println();

            System.out.println("=== DEMO 5: Optional dependencies ===");
            OrderNotifier notifier = context.getBean(OrderNotifier.class);
            System.out.println("   " + notifier.notifyCustomer("+91-90000-11111", "Your food is on the way"));
            System.out.println();
        }

        System.out.println("=== DEMO 6: Circular dependency ===");
        System.out.println("   Starting a container for package com.course.circular.broken ...");
        try (AnnotationConfigApplicationContext broken =
                     new AnnotationConfigApplicationContext("com.course.circular.broken")) {
            System.out.println("   (should never get here)");
        } catch (Exception e) {
            // Walk down to the deepest cause - that is where the real explanation is.
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            System.out.println("   FAILED as expected -> " + root.getClass().getSimpleName());
            System.out.println("   " + root.getMessage());
        }

        System.out.println("   Starting a container for package com.course.circular.fixed ...");
        try (AnnotationConfigApplicationContext fixed =
                     new AnnotationConfigApplicationContext("com.course.circular.fixed")) {
            FixedPricingService pricing = fixed.getBean(FixedPricingService.class);
            FixedInventoryService inventory = fixed.getBean(FixedInventoryService.class);
            System.out.println("   Fixed design works! Paneer Tikka in stock? " + inventory.inStock("Paneer Tikka")
                    + ", price (only 2 left): " + pricing.priceOf("Paneer Tikka"));
        }
        System.out.println("=== Done ===");
    }
}
