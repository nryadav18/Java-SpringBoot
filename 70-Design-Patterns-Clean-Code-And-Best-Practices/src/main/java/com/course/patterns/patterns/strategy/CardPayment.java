package com.course.patterns.patterns.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/* Strategy #2 - bean name "card" (2% card fee). */
@Component("card")
public class CardPayment implements PaymentStrategy {

    private static final BigDecimal CARD_FEE_RATE = new BigDecimal("0.02");

    @Override
    public String pay(BigDecimal amount) {
        BigDecimal fee = amount.multiply(CARD_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        return "Card payment of " + amount + " + fee " + fee;
    }
}
