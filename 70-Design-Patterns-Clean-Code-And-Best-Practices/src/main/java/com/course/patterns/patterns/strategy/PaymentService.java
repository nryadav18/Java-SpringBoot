package com.course.patterns.patterns.strategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/*
 * PaymentService.java - the CONTEXT of the Strategy pattern, the Spring way.
 *
 * Spring injects a Map<String, PaymentStrategy>: key = bean name, value = bean.
 *   { "upi" -> UpiPayment, "card" -> CardPayment, "wallet" -> WalletPayment }
 * No if/else, no switch. A new payment method = a new @Component. This class never changes.
 *
 * Other Strategy examples INSIDE Spring: PasswordEncoder (BCrypt, Argon2...),
 * ResourceLoader, CacheManager, the HttpMessageConverter chosen per content type.
 */
@Service
public class PaymentService {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentService(Map<String, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public String checkout(String method, BigDecimal amount) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method
                    + ". Available: " + strategies.keySet());
        }
        return strategy.pay(amount);
    }

    public Set<String> availableMethods() {
        return strategies.keySet();
    }
}
