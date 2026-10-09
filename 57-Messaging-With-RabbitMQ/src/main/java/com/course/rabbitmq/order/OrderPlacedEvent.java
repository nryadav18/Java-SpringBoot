package com.course.rabbitmq.order;

import java.math.BigDecimal;
import java.time.Instant;

/*
 * The MESSAGE we put into RabbitMQ when an order is placed.
 * It is an immutable record = a fact that happened ("order 42 was placed").
 * It travels as JSON, for example:
 *   {"orderId":"a1b2...","customerName":"Asha","customerEmail":"asha@example.com",
 *    "product":"Laptop","quantity":1,"totalAmount":899.99,"placedAt":"2026-10-09T05:00:00Z"}
 */
public record OrderPlacedEvent(
        String orderId,
        String customerName,
        String customerEmail,
        String product,
        int quantity,
        BigDecimal totalAmount,
        Instant placedAt) {
}
