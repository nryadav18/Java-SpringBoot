package com.course.apiclient.web;

import com.course.apiclient.exception.ExternalResourceNotFoundException;
import com.course.apiclient.exception.ExternalServiceException;
import com.course.apiclient.exception.ExternalServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

/*
 * Translates "the other API had a problem" into good answers for OUR clients.
 *
 *   remote 404                 -> we answer 404 Not Found
 *   remote 4xx/5xx             -> we answer 502 Bad Gateway   ("the server behind me failed")
 *   no answer / timeout        -> we answer 503 Service Unavailable
 *
 * We never forward the remote error body or stack trace as-is: it may contain
 * internal details, and our clients should not depend on another company's format.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ExternalResourceNotFoundException.class)
    public ProblemDetail notFound(ExternalResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ProblemDetail remoteError(ExternalServiceException ex) {
        log.warn("Remote API error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY,
                "The external service returned an error");
        problem.setProperty("remoteStatus", ex.getRemoteStatus());
        return problem;
    }

    // ResourceAccessException can also come straight from the HTTP-interface proxy
    @ExceptionHandler({ExternalServiceUnavailableException.class, ResourceAccessException.class})
    public ProblemDetail unavailable(RuntimeException ex) {
        log.warn("Remote API unavailable: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "The external service is not reachable right now. Please try again later.");
    }
}
