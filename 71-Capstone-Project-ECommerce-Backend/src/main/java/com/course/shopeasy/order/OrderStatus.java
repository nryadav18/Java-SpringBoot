package com.course.shopeasy.order;

import java.util.Set;

/**
 * The life of an order, as a tiny state machine:
 * <pre>
 *   PENDING --> PAID --> SHIPPED --> DELIVERED
 *      |          |
 *      +----------+-----> CANCELLED
 * </pre>
 * Keeping the allowed moves INSIDE the enum means no service can, for example, jump from
 * DELIVERED back to PENDING by mistake (topic 15: enums with behaviour).
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public boolean canMoveTo(OrderStatus next) {
        return allowedNext().contains(next);
    }

    private Set<OrderStatus> allowedNext() {
        return switch (this) {                       // Java 14+ switch expression
            case PENDING -> Set.of(PAID, CANCELLED);
            case PAID -> Set.of(SHIPPED, CANCELLED);
            case SHIPPED -> Set.of(DELIVERED);
            case DELIVERED, CANCELLED -> Set.of();   // final states
        };
    }
}
