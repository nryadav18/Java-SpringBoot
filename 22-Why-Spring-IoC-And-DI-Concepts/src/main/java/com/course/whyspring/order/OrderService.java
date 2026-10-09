/*
 * STEP 2 - THE FIX, PART 2: CONSTRUCTOR INJECTION.
 *
 * OrderService no longer creates its own notifier with "new".
 * Instead it ASKS for one in its constructor. Somebody else (the "assembler")
 * decides which implementation to give it. This is DEPENDENCY INJECTION (DI).
 *
 * Benefits:
 *   - OrderService only knows the INTERFACE  -> loose coupling
 *   - Switch Email -> SMS without touching this file
 *   - In a test we can pass a fake NotificationService (see OrderServiceTest)
 *   - The dependency is visible in the constructor - nothing hidden
 *
 * IMPORTANT: there is NO Spring code in this class at all. It is plain Java.
 * The same class is used by the manual assembler (step 2) AND by Spring (step 3).
 */
package com.course.whyspring.order;

import com.course.whyspring.notification.NotificationService;

public class OrderService {

    // "final" = must be set once, in the constructor, and can never be replaced later.
    private final NotificationService notificationService;

    // The dependency is INJECTED (handed in) from outside.
    public OrderService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public String placeOrder(String customer, String item) {
        System.out.println("   Order placed: " + item + " for " + customer
                + " (notify via " + notificationService.channel() + ")");
        String message = "Your order for " + item + " is confirmed!";
        notificationService.send(customer, message);
        return message;
    }
}
