package com.course.cicd.shipping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

/*
 * ShippingService.java - the business rule we want CI to protect with tests.
 *
 * Rules of our imaginary online shop:
 *   cost = 5.00 base fee + 2.00 per kg
 *   express delivery = cost x 1.5
 *   weight must be > 0 and <= 30 kg, otherwise IllegalArgumentException
 *
 * If somebody changes a rule by mistake (e.g. 2.00 -> 20.00), the tests in
 * ShippingServiceTest fail, the CI pipeline turns RED, and the pull request
 * cannot be merged. That is the whole point of CI.
 */
@Service
public class ShippingService {

    // Named constants instead of "magic numbers" (clean code, topic 70)
    static final BigDecimal BASE_FEE = new BigDecimal("5.00");
    static final BigDecimal PRICE_PER_KG = new BigDecimal("2.00");
    static final BigDecimal EXPRESS_MULTIPLIER = new BigDecimal("1.5");
    static final double MAX_WEIGHT_KG = 30.0;

    public ShippingQuote quote(double weightKg, boolean express) {
        if (weightKg <= 0 || weightKg > MAX_WEIGHT_KG) {
            throw new IllegalArgumentException(
                    "Weight must be greater than 0 and at most " + MAX_WEIGHT_KG + " kg, but was " + weightKg);
        }

        // BigDecimal for money: double cannot store 0.1 exactly (topic 2)
        BigDecimal cost = BASE_FEE.add(PRICE_PER_KG.multiply(BigDecimal.valueOf(weightKg)));
        if (express) {
            cost = cost.multiply(EXPRESS_MULTIPLIER);
        }
        return new ShippingQuote(weightKg, express, cost.setScale(2, RoundingMode.HALF_UP));
    }
}
