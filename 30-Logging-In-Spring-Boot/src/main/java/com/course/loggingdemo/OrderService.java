package com.course.loggingdemo;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/*
 * Business logic for orders, with GOOD logging habits:
 *   - one logger per class (private static final)
 *   - parameterized messages with {} placeholders (no string concatenation)
 *   - the right LEVEL for each message (TRACE < DEBUG < INFO < WARN < ERROR)
 *   - exceptions logged WITH the stack trace (exception as the LAST argument)
 *   - sensitive data (card numbers) is masked, never logged in full
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final int MAX_QUANTITY = 20;

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1000);

    public Order placeOrder(String customer, String item, int quantity, String cardNumber) {
        // TRACE: extremely detailed, usually switched OFF. Good for "step by step" debugging.
        log.trace("placeOrder() called with customer={}, item={}, quantity={}", customer, item, quantity);

        // DEBUG: details useful for developers while fixing a problem.
        log.debug("Validating order for customer {}", customer);

        if (quantity <= 0 || quantity > MAX_QUANTITY) {
            // WARN: something unexpected, but the app handles it and keeps working.
            log.warn("Rejected order from {}: invalid quantity {} (allowed 1-{})", customer, quantity, MAX_QUANTITY);
            throw new IllegalArgumentException("Quantity must be between 1 and " + MAX_QUANTITY);
        }

        // NEVER log the full card number. Mask it first.
        log.debug("Charging card {}", maskCard(cardNumber));

        long id = idSequence.incrementAndGet();
        Order order = new Order(id, customer, item, quantity, "PLACED");
        orders.put(id, order);

        // INFO: important business events - "what happened". These are what you read in production.
        log.info("Order {} placed: {} x {} for customer {}", id, quantity, item, customer);
        return order;
    }

    public Optional<Order> findOrder(long id) {
        Order order = orders.get(id);
        if (order == null) {
            log.warn("Order {} not found", id);
        }
        return Optional.ofNullable(order);
    }

    public void notifyRestaurant(long orderId) {
        try {
            // Pretend the restaurant system is down.
            throw new IllegalStateException("Restaurant system timed out");
        } catch (IllegalStateException ex) {
            // ERROR: something failed and needs attention.
            // Passing "ex" as the LAST argument makes SLF4J print the full stack trace.
            log.error("Could not notify restaurant for order {}", orderId, ex);
        }
    }

    /** "4111111111111111" -> "************1111". Package-private so the test can use it. */
    static String maskCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "*".repeat(cardNumber.length() - 4) + cardNumber.substring(cardNumber.length() - 4);
    }
}
