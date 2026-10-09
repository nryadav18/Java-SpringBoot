package com.course.patterns.patterns.strategy;

import com.course.patterns.demo.PatternDemo;
import java.math.BigDecimal;
import java.util.TreeSet;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * STRATEGY demo - choose the algorithm at runtime by name.
 *
 * Expected output:
 *   Available methods: [card, upi, wallet]
 *   upi    -> UPI payment of 1000.00 (no fee)
 *   card   -> Card payment of 1000.00 + fee 20.00
 *   wallet -> Wallet payment of 1000.00 (cashback 1%)
 *   cash   -> Unsupported payment method: cash. Available: [...]
 */
@Component
@Order(14)
public class StrategyDemo implements PatternDemo {

    private final PaymentService paymentService;

    public StrategyDemo(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public String title() {
        return "Strategy - interchangeable algorithms (Spring injects a Map of beans)";
    }

    @Override
    public void run() {
        System.out.println("Available methods: " + new TreeSet<>(paymentService.availableMethods()));
        BigDecimal amount = new BigDecimal("1000.00");
        for (String method : new String[] {"upi", "card", "wallet"}) {
            System.out.printf("%-6s -> %s%n", method, paymentService.checkout(method, amount));
        }
        try {
            paymentService.checkout("cash", amount);
        } catch (IllegalArgumentException e) {
            System.out.printf("%-6s -> %s%n", "cash", e.getMessage());
        }
    }
}
