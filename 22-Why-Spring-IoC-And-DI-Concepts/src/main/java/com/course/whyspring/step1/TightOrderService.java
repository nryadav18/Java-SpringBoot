/*
 * STEP 1 - THE PROBLEM (tight coupling)
 *
 * This OrderService creates its own EmailSender with "new".
 * It works... but look at the problems:
 *
 *   1. HARD TO CHANGE:  the business wants SMS instead of email?
 *      We must open this class and edit it (and every other class that does "new EmailSender()").
 *   2. HARD TO TEST:    a unit test of placeOrder() will ALWAYS send a real email.
 *      We cannot swap in a fake sender.
 *   3. HIDDEN DEPENDENCY: from the outside you cannot see that OrderService needs an EmailSender.
 *
 * This is called TIGHT COUPLING: OrderService is "glued" to one concrete class.
 */
package com.course.whyspring.step1;

public class TightOrderService {

    // The class decides AND creates its own dependency. This "new" is the glue.
    private final EmailSender emailSender = new EmailSender();

    public void placeOrder(String customer, String item) {
        System.out.println("   Order placed: " + item + " for " + customer);
        emailSender.sendEmail(customer, "Your order for " + item + " is confirmed!");
    }
}
