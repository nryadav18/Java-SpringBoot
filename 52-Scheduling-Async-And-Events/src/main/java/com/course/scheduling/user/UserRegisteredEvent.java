package com.course.scheduling.user;

import java.time.LocalDateTime;

/*
 * The EVENT: a small, immutable message that says "this happened".
 *
 * - Named in the PAST tense: UserRegistered, OrderPlaced, PaymentFailed.
 * - Carries only the data listeners need (ids and simple values, NOT the JPA entity).
 * - Since Spring 4.2 any object can be an event - no need to extend ApplicationEvent.
 */
public record UserRegisteredEvent(Long userId, String name, String email, LocalDateTime registeredAt) {
}
