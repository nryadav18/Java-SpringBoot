/*
 * The contract for paying money. Our food-delivery app has THREE implementations
 * (card, UPI, PayPal) - which creates the classic Spring question:
 * "Which one should I inject?"  ->  answered with @Primary and @Qualifier.
 */
package com.course.di.payment;

public interface PaymentGateway {

    /** Pays the amount and returns a short receipt text. */
    String pay(double amount);

    /** Human-friendly name of the payment method. */
    String displayName();
}
