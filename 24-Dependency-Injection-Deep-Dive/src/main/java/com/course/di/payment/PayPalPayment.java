/*
 * Implementation #3 - PayPal.
 *
 * Custom bean name "paypal" (instead of the default "payPalPayment").
 * The name becomes the KEY when all gateways are injected as a Map<String, PaymentGateway>.
 */
package com.course.di.payment;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component("paypal")
@Order(3)
public class PayPalPayment implements PaymentGateway {

    @Override
    public String pay(double amount) {
        return "Paid " + amount + " by PAYPAL";
    }

    @Override
    public String displayName() {
        return "PayPal";
    }
}
