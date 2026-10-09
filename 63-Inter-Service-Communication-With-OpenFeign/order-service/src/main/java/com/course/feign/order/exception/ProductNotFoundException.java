/*
 * ProductNotFoundException - product-service answered 404: the product does not exist.
 * Unchecked (extends RuntimeException) so it does not clutter method signatures.
 * GlobalExceptionHandler turns it into a clean 404 for OUR callers.
 */
package com.course.feign.order.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
