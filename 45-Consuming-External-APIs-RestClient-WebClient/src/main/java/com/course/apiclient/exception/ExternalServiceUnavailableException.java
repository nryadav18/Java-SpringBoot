package com.course.apiclient.exception;

/*
 * We could not get ANY answer: connection refused, DNS failure, no internet,
 * or the other API was too slow (timeout).
 * For OUR client this becomes 503 Service Unavailable.
 */
public class ExternalServiceUnavailableException extends RuntimeException {

    public ExternalServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
