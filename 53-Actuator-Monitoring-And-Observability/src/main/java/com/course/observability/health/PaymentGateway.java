package com.course.observability.health;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

/*
 * A FAKE external payment provider ("DemoPay").
 * In real life this would be an HTTP client for Stripe, Razorpay, PayPal ...
 * We can switch it "down" through an admin endpoint to watch the health status change.
 */
@Component
public class PaymentGateway {

    private final AtomicBoolean up = new AtomicBoolean(true);

    public boolean isUp() {
        return up.get();
    }

    public void setUp(boolean value) {
        up.set(value);
    }

    /** Pretend to "ping" the provider and return the round-trip time in ms. */
    public long ping() {
        if (!up.get()) {
            throw new IllegalStateException("Connection refused: api.demopay.example:443");
        }
        return 42;
    }
}
