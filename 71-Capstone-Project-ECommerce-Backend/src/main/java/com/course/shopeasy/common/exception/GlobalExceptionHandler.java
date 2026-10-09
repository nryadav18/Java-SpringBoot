package com.course.shopeasy.common.exception;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

/**
 * ONE place that turns every exception into a clean JSON error (topic 35).
 *
 * We return Spring's built-in ProblemDetail (RFC 9457 "application/problem+json"):
 * <pre>
 * {
 *   "type": "https://shopeasy.example/problems/insufficient-stock",
 *   "title": "Insufficient stock",
 *   "status": 409,
 *   "detail": "Not enough stock for 'Smart Watch Fit': requested 5, available 3",
 *   "instance": "/api/v1/orders",
 *   "timestamp": "2026-10-09T10:15:30Z"
 * }
 * </pre>
 *
 * Extending ResponseEntityExceptionHandler gives us ProblemDetail responses for all of Spring MVC's own
 * exceptions (bad JSON, wrong HTTP method, missing parameter ...) for free. We only add our own cases.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_BASE_URI = "https://shopeasy.example/problems/";

    // ---------------------------------------------------------------- our business exceptions

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Resource not found", "not-found", ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        return problem(HttpStatus.CONFLICT, "Duplicate resource", "duplicate", ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    ProblemDetail handleInsufficientStock(InsufficientStockException ex) {
        ProblemDetail pd = problem(HttpStatus.CONFLICT, "Insufficient stock", "insufficient-stock", ex.getMessage());
        // Extra machine-readable fields help a front-end show "only 3 left!"
        pd.setProperty("productId", ex.getProductId());
        pd.setProperty("requested", ex.getRequested());
        pd.setProperty("available", ex.getAvailable());
        return pd;
    }

    @ExceptionHandler(BusinessRuleException.class)
    ProblemDetail handleBusinessRule(BusinessRuleException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Business rule violated", "business-rule", ex.getMessage());
    }

    // ---------------------------------------------------------------- security & persistence

    /** Wrong e-mail or password on login. Same message for both, so attackers cannot probe which e-mails exist. */
    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    ProblemDetail handleBadCredentials(RuntimeException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed", "authentication-failed",
                "Invalid email or password");
    }

    /**
     * @PreAuthorize failures are thrown INSIDE the controller call, so they arrive here and not at the
     * security filter. Without this handler the catch-all below would wrongly turn them into a 500.
     */
    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", "forbidden",
                "You do not have permission to perform this action");
    }

    /** Two transactions changed the same product row at the same time (@Version check failed - topic 42). */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return problem(HttpStatus.CONFLICT, "Concurrent update", "concurrent-update",
                "The data was changed by someone else. Please reload and try again.");
    }

    /** ?sort=doesNotExist,asc  -> 400 instead of a 500. */
    @ExceptionHandler(PropertyReferenceException.class)
    ProblemDetail handleBadSortProperty(PropertyReferenceException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid sort property", "invalid-sort",
                "Cannot sort by '%s'".formatted(ex.getPropertyName()));
    }

    /** Violations of @Min/@Max etc. on @RequestParam / @PathVariable (needs @Validated on the controller). */
    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Validation failed", "validation", "Request parameters are invalid");
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        pd.setProperty("errors", errors);
        return pd;
    }

    /** Safety net: anything unexpected. Log the full stack trace for US, send a generic message to the CLIENT. */
    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "internal",
                "Something went wrong on our side. Please try again later.");
    }

    // ---------------------------------------------------------------- @Valid on @RequestBody

    /**
     * Called by Spring when a @Valid @RequestBody fails validation.
     * We add a field -> message map so the client knows exactly which input to fix:
     * "errors": { "email": "must be a well-formed email address", "password": "size must be ..." }
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Validation failed", "validation",
                "One or more fields are invalid");
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        ex.getBindingResult().getGlobalErrors()
                .forEach(ge -> errors.putIfAbsent(ge.getObjectName(), ge.getDefaultMessage()));
        pd.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(pd);
    }

    /** Adds our timestamp to the ProblemDetails Spring builds for its own exceptions as well. */
    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers,
                                                          HttpStatusCode statusCode, WebRequest request) {
        if (body instanceof ProblemDetail pd && (pd.getProperties() == null || !pd.getProperties().containsKey("timestamp"))) {
            pd.setProperty("timestamp", Instant.now());
        }
        return super.createResponseEntity(body, headers, statusCode, request);
    }

    // ---------------------------------------------------------------- helper

    private static ProblemDetail problem(HttpStatus status, String title, String type, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(PROBLEM_BASE_URI + type));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
