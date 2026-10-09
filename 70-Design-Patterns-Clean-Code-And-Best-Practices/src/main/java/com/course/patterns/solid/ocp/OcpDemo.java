package com.course.patterns.solid.ocp;

import com.course.patterns.demo.PatternDemo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * O - OPEN/CLOSED PRINCIPLE
 * "Software should be OPEN for extension but CLOSED for modification."
 * You add new behaviour by adding NEW code, not by editing old, tested code.
 *
 * BAD : a growing if/else chain. Every new customer type = edit this method again
 *       (and maybe break the existing types).
 * GOOD: a Discount interface. A new "StudentDiscount" is a new class; Checkout never changes.
 *
 * Expected output:
 *   [bad ] PREMIUM pays 900.00
 *   [good] Regular pays 1000.00
 *   [good] Premium pays 900.00
 *   [good] Student pays 850.00   <- added later without touching Checkout
 */
@Component
@Order(2)
public class OcpDemo implements PatternDemo {

    // ---------------------------------------------------------- BAD
    static BigDecimal priceBad(String customerType, BigDecimal amount) {
        if (customerType.equals("REGULAR")) {
            return amount;
        } else if (customerType.equals("PREMIUM")) {
            return amount.multiply(new BigDecimal("0.90"));
        }
        // ...next month: else if ("STUDENT") ... and again ... and again
        return amount;
    }

    // ---------------------------------------------------------- GOOD
    /** The extension point. */
    interface Discount {
        String name();

        BigDecimal apply(BigDecimal amount);
    }

    static class RegularDiscount implements Discount {
        public String name() { return "Regular"; }
        public BigDecimal apply(BigDecimal amount) { return amount; }
    }

    static class PremiumDiscount implements Discount {
        public String name() { return "Premium"; }
        public BigDecimal apply(BigDecimal amount) { return amount.multiply(new BigDecimal("0.90")); }
    }

    /** NEW requirement -> NEW class. Nothing else was edited. */
    static class StudentDiscount implements Discount {
        public String name() { return "Student"; }
        public BigDecimal apply(BigDecimal amount) { return amount.multiply(new BigDecimal("0.85")); }
    }

    /** Closed for modification: works with ANY Discount, present or future. */
    static class Checkout {
        BigDecimal total(BigDecimal amount, Discount discount) {
            return discount.apply(amount).setScale(2, RoundingMode.HALF_UP);
        }
    }

    @Override
    public String title() {
        return "O - Open/Closed Principle";
    }

    @Override
    public void run() {
        BigDecimal amount = new BigDecimal("1000");
        System.out.println("[bad ] PREMIUM pays " + priceBad("PREMIUM", amount).setScale(2, RoundingMode.HALF_UP));

        Checkout checkout = new Checkout();
        for (Discount d : new Discount[] {new RegularDiscount(), new PremiumDiscount(), new StudentDiscount()}) {
            System.out.println("[good] " + d.name() + " pays " + checkout.total(amount, d));
        }
    }
}
