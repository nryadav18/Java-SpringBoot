package com.course.patterns.patterns.strategy;

import java.math.BigDecimal;

/*
 * STRATEGY (behavioural pattern) - the common interface.
 * "A family of interchangeable algorithms behind one interface; choose one at runtime."
 *
 * Each implementation is a Spring bean with a NAME ("upi", "card", "wallet").
 * PaymentService receives ALL of them as Map<String, PaymentStrategy> and picks one per request.
 */
public interface PaymentStrategy {

    /** Returns a human-readable receipt line. */
    String pay(BigDecimal amount);
}
