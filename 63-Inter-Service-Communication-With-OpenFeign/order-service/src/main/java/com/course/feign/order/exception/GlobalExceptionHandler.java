/*
 * GlobalExceptionHandler - one place that turns exceptions into clean HTTP answers (topic 35).
 * We use ProblemDetail (RFC 9457) which Spring 6 supports out of the box.
 *
 * Mapping:
 *   ProductNotFoundException            -> 404 NOT FOUND
 *   OutOfStockException                 -> 409 CONFLICT
 *   ProductServiceUnavailableException  -> 503 SERVICE UNAVAILABLE
 *   feign.RetryableException            -> 504 GATEWAY TIMEOUT  (connection refused / read timeout)
 */
package com.course.feign.order.exception;

import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleNotFound(ProductNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Product not found", ex.getMessage());
    }

    @ExceptionHandler(OutOfStockException.class)
    public ProblemDetail handleOutOfStock(OutOfStockException ex) {
        return problem(HttpStatus.CONFLICT, "Out of stock", ex.getMessage());
    }

    @ExceptionHandler(ProductServiceUnavailableException.class)
    public ProblemDetail handleUnavailable(ProductServiceUnavailableException ex) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "Product service unavailable", ex.getMessage());
    }

    /**
     * RetryableException = Feign could not get ANY answer: product-service is not running
     * (connection refused) or it was slower than our read-timeout.
     */
    @ExceptionHandler(RetryableException.class)
    public ProblemDetail handleNoAnswer(RetryableException ex) {
        log.error("No answer from product-service: {}", ex.getMessage());
        return problem(HttpStatus.GATEWAY_TIMEOUT, "Product service did not answer",
                "product-service is down or too slow. Please try again later.");
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        return pd;
    }
}
