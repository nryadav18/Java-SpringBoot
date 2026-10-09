package com.course.kafka.order;

import java.math.BigDecimal;
import java.time.Instant;

/*
 * One EVENT = one fact that happened to an order. Events are never changed;
 * new facts are simply appended to the topic (like lines in a diary).
 *
 * It is stored in Kafka as JSON (value) with the orderId as the KEY:
 *   key:   "7c9e..."
 *   value: {"eventId":"...","orderId":"7c9e...","customerName":"Asha",
 *           "status":"PAID","amount":899.99,"occurredAt":"2026-10-09T05:00:00Z"}
 */
public record OrderEvent(
        String eventId,
        String orderId,
        String customerName,
        OrderStatus status,
        BigDecimal amount,
        Instant occurredAt) {
}
