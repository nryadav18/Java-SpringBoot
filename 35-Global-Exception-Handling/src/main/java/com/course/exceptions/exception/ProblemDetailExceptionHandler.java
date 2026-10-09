package com.course.exceptions.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/*
 * ProblemDetailExceptionHandler - the STANDARD error format (RFC 9457, formerly RFC 7807).
 * ----------------------------------------------------------------------------------------
 * Active only with:  mvn spring-boot:run -Dspring-boot.run.profiles=problem
 *
 * ProblemDetail is a class built into Spring Framework 6. It produces JSON with
 * Content-Type "application/problem+json" and these standard fields:
 *   type     - a URI that identifies the KIND of problem (default "about:blank")
 *   title    - short, human-readable summary ("Not Found")
 *   status   - the HTTP status code
 *   detail   - explanation for THIS occurrence ("Book not found with id: 99")
 *   instance - the URI of the request that failed (filled in automatically)
 * plus any extra "properties" you add (timestamp, fieldErrors, ...).
 *
 * Extending ResponseEntityExceptionHandler gives us ready-made ProblemDetail
 * handling for ~20 Spring MVC exceptions (bad JSON, 405, unknown URL, type mismatch,
 * parameter validation ...). We only add OUR exceptions and customise validation.
 */
@RestControllerAdvice
@Profile("problem")
public class ProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ProblemDetailExceptionHandler.class);
    private static final String TYPE_BASE = "https://api.library.example/problems/";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create(TYPE_BASE + "resource-not-found"));
        problem.setTitle("Resource not found");
        problem.setProperty("resource", ex.getResourceName());      // extra, custom properties
        problem.setProperty("timestamp", Instant.now());
        return problem;                                            // returning ProblemDetail is enough
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create(TYPE_BASE + "duplicate-resource"));
        problem.setTitle("Duplicate resource");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ProblemDetail handleNotAvailable(BookNotAvailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setType(URI.create(TYPE_BASE + "book-not-available"));
        problem.setTitle("Book not available");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    /** Customise the built-in handling of @Valid failures: add a "fieldErrors" list. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail problem = ex.getBody();                      // Spring already built a ProblemDetail
        problem.setType(URI.create(TYPE_BASE + "validation-error"));
        problem.setTitle("Validation failed");
        problem.setDetail("One or more fields are invalid");
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", String.valueOf(fe.getDefaultMessage())))
                .toList();
        problem.setProperty("fieldErrors", fieldErrors);
        problem.setProperty("timestamp", Instant.now());
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    /** Safety net - never leak internals. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong on our side. Please try again later.");
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
