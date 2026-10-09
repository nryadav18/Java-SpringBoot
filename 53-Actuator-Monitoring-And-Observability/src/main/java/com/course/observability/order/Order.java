package com.course.observability.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/* An order in our shop (immutable record). */
public record Order(Long id, String product, BigDecimal amount, String paymentMethod,
                    OrderStatus status, LocalDateTime createdAt) {

    public enum OrderStatus { PENDING, PAID, FAILED }
}
