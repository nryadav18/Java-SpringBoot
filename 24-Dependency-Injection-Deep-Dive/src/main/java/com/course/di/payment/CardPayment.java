/*
 * Implementation #1 - card payment.
 *
 * @Primary   -> "If someone asks for a PaymentGateway and doesn't say which one,
 *                give them ME." It is the default choice among several candidates.
 * @Order(1)  -> position when ALL PaymentGateways are injected as a List (first).
 *
 * Bean name (default): "cardPayment"
 */
package com.course.di.payment;

import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Primary
@Order(1)
public class CardPayment implements PaymentGateway {

    @Override
    public String pay(double amount) {
        return "Paid " + amount + " by CARD";
    }

    @Override
    public String displayName() {
        return "Credit/Debit Card";
    }
}
