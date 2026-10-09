package com.course.apiclient.exception;

/*
 * The OTHER API answered 404: the thing we asked for does not exist there.
 * Our ApiExceptionHandler turns this into a 404 for OUR client too.
 */
public class ExternalResourceNotFoundException extends RuntimeException {

    public ExternalResourceNotFoundException(String message) {
        super(message);
    }
}
