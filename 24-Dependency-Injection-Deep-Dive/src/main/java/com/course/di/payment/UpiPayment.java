/*
 * Implementation #2 - UPI payment (India's instant mobile payment system).
 *
 * Bean name (default): "upiPayment"  - used with @Qualifier("upiPayment") in ExpressCheckoutService.
 */
package com.course.di.payment;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class UpiPayment implements PaymentGateway {

    @Override
    public String pay(double amount) {
        return "Paid " + amount + " by UPI";
    }

    @Override
    public String displayName() {
        return "UPI";
    }
}
