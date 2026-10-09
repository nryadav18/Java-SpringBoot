package com.course.patterns.patterns.strategy;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/* Strategy #3 - bean name "wallet". Added later: NO other class had to change (Open/Closed!). */
@Component("wallet")
public class WalletPayment implements PaymentStrategy {

    @Override
    public String pay(BigDecimal amount) {
        return "Wallet payment of " + amount + " (cashback 1%)";
    }
}
