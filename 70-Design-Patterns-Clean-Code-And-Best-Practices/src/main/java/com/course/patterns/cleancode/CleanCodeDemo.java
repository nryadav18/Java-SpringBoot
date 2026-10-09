package com.course.patterns.cleancode;

import com.course.patterns.demo.PatternDemo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * CLEAN CODE - the same logic written twice.
 *
 * BEFORE: short cryptic names, magic numbers (1, 2, 0.9, 40), deep nesting,
 *         double for money, a boolean flag nobody understands at the call site.
 * AFTER : intention-revealing names, an enum instead of "int t", named constants,
 *         guard clause, tiny methods, BigDecimal for money.
 *
 * Both give the same result - but only one can be read, reviewed and changed safely.
 *
 * Expected output:
 *   [before] calc(2, 1000.0, true) = 840.0
 *   [after ] finalPrice(1000, GOLD, EXPRESS) = 840.00
 */
@Component
@Order(30)
public class CleanCodeDemo implements PatternDemo {

    // ================================================================ BEFORE
    static class Before {
        // What is t? What is f? Why 0.9? Why 40?
        static double calc(int t, double a, boolean f) {
            double r = 0;
            if (a > 0) {
                if (t == 1) {
                    r = a * 0.9;
                } else {
                    if (t == 2) {
                        r = a * 0.8;
                    } else {
                        r = a;
                    }
                }
                if (f) {
                    r = r + 40;
                }
            }
            return r;
        }
    }

    // ================================================================ AFTER
    static class After {

        enum CustomerTier {
            REGULAR("0"), SILVER("10"), GOLD("20");

            private final BigDecimal discountPercent;

            CustomerTier(String discountPercent) {
                this.discountPercent = new BigDecimal(discountPercent);
            }
        }

        enum Delivery { STANDARD, EXPRESS }

        private static final BigDecimal EXPRESS_DELIVERY_FEE = new BigDecimal("40");
        private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

        static BigDecimal finalPrice(BigDecimal orderAmount, CustomerTier tier, Delivery delivery) {
            requirePositive(orderAmount);                       // guard clause: fail fast, no nesting
            BigDecimal discounted = applyTierDiscount(orderAmount, tier);
            return addDeliveryFee(discounted, delivery).setScale(2, RoundingMode.HALF_UP);
        }

        private static void requirePositive(BigDecimal amount) {
            if (amount == null || amount.signum() <= 0) {
                throw new IllegalArgumentException("Order amount must be positive, but was " + amount);
            }
        }

        private static BigDecimal applyTierDiscount(BigDecimal amount, CustomerTier tier) {
            BigDecimal discount = amount.multiply(tier.discountPercent).divide(ONE_HUNDRED);
            return amount.subtract(discount);
        }

        private static BigDecimal addDeliveryFee(BigDecimal amount, Delivery delivery) {
            return delivery == Delivery.EXPRESS ? amount.add(EXPRESS_DELIVERY_FEE) : amount;
        }
    }

    @Override
    public String title() {
        return "Clean code - before and after";
    }

    @Override
    public void run() {
        System.out.println("[before] calc(2, 1000.0, true) = " + Before.calc(2, 1000.0, true));
        System.out.println("[after ] finalPrice(1000, GOLD, EXPRESS) = "
                + After.finalPrice(new BigDecimal("1000"), After.CustomerTier.GOLD, After.Delivery.EXPRESS));
    }
}
