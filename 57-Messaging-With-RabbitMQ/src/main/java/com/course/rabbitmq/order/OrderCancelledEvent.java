package com.course.rabbitmq.order;

import java.time.Instant;

/* Message sent when an order is cancelled (routing key "order.cancelled"). */
public record OrderCancelledEvent(String orderId, String reason, Instant cancelledAt) {
}
