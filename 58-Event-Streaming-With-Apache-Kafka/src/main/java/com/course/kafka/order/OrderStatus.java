package com.course.kafka.order;

/* The kinds of events in an order's life. */
public enum OrderStatus {
    CREATED, PAID, SHIPPED, CANCELLED
}
