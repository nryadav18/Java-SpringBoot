package com.course.shopeasy.common.exception;

/**
 * Thrown when a request is well-formed but breaks a business rule, for example:
 *   - placing an order with an empty cart
 *   - cancelling an order that was already shipped
 *   - deleting a category that still has products
 * Mapped to HTTP 422 Unprocessable Content ("I understood you, but I cannot do that").
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
