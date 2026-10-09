package com.course.patterns.patterns.strategy;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/* Strategy #1 - bean name "upi" (no fee). */
@Component("upi")
public class UpiPayment implements PaymentStrategy {

    @Override
    public String pay(BigDecimal amount) {
        return "UPI payment of " + amount + " (no fee)";
    }
}
