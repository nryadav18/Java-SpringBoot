/*
 * ProductServiceUnavailableException - product-service answered 5xx.
 * GlobalExceptionHandler turns it into a 503 SERVICE UNAVAILABLE for OUR callers.
 * (Topic 64 shows how to add a circuit breaker + fallback instead of just failing.)
 */
package com.course.feign.order.exception;

public class ProductServiceUnavailableException extends RuntimeException {

    public ProductServiceUnavailableException(String message) {
        super(message);
    }
}
