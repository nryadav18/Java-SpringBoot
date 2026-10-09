/*
 * OutOfStockException - not enough stock (either we checked it, or product-service answered 409).
 * GlobalExceptionHandler turns it into a 409 CONFLICT for OUR callers.
 */
package com.course.feign.order.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String message) {
        super(message);
    }
}
