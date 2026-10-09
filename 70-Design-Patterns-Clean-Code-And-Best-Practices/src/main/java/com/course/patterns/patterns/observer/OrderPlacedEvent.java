package com.course.patterns.patterns.observer;

import java.math.BigDecimal;

/*
 * OrderPlacedEvent.java - the "news" that is announced to all observers.
 * Since Spring 4.2 any object (here a record) can be an application event.
 */
public record OrderPlacedEvent(String orderId, String customerEmail, BigDecimal total) {
}
