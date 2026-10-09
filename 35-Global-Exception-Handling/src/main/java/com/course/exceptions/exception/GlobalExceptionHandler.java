package com.course.exceptions.exception;

import com.course.exceptions.dto.ErrorResponse;
import com.course.exceptions.dto.ErrorResponse.FieldErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/*
 * GlobalExceptionHandler - ONE place that turns exceptions into HTTP responses.
 * ------------------------------------------------------------------------------
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   "Apply these @ExceptionHandler methods to ALL controllers, and write the
 *    return value as JSON."
 *
 * How Spring picks a handler: it looks for the @ExceptionHandler whose exception
 * type is the CLOSEST match (most specific). So ResourceNotFoundException goes to
 * handleNotFound(...), and only exceptions with no better match fall through to
 * the catch-all handleUnexpected(Exception).
 *
 * @Profile("!problem & !plain"): active by default; switched off when you run
 * with the "problem" profile (ProblemDetail style) or "plain" (Spring defaults).
 */
@RestControllerAdvice
@Profile("!problem & !plain")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------------------------------------------------------------------
    // 1. OUR OWN business exceptions
    // ---------------------------------------------------------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,
                                                        HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex,
                                                         HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleNotAvailable(BookNotAvailableException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    // ---------------------------------------------------------------------
    // 2. Validation errors (topic 34)
    // ---------------------------------------------------------------------

    /** @Valid @RequestBody failed -> 400 with a clean list of field errors. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldErrorDetail(fe.getField(), fe.getDefaultMessage()))
                .sorted((a, b) -> a.field().compareTo(b.field()))   // stable order for clients
                .toList();
        ErrorResponse body = new ErrorResponse(java.time.Instant.now(), 400, "Bad Request",
                "Validation failed for " + fieldErrors.size() + " field(s)",
                request.getRequestURI(), fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /** Constraint on a @PathVariable / @RequestParam failed (built-in method validation, Spring 6.1+). */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleParamValidation(HandlerMethodValidationException ex,
                                                               HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new FieldErrorDetail(
                                result.getMethodParameter().getParameterName(),
                                error.getDefaultMessage())))
                .toList();
        ErrorResponse body = new ErrorResponse(java.time.Instant.now(), 400, "Bad Request",
                "Invalid request parameter(s)", request.getRequestURI(), fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    // ---------------------------------------------------------------------
    // 3. Common "the client sent something wrong" errors from Spring MVC
    // ---------------------------------------------------------------------

    /** Broken JSON, e.g. a missing quote or "copies": "many". */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Request body is missing or is not valid JSON", request);
    }

    /** /api/books/abc where a number is expected. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String expected = ex.getRequiredType() == null ? "another type" : ex.getRequiredType().getSimpleName();
        String message = "Parameter '" + ex.getName() + "' must be of type " + expected
                + " but was '" + ex.getValue() + "'";
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    /** DELETE on a URL that only supports GET, etc. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                                                HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP method " + ex.getMethod() + " is not supported for this URL", request);
    }

    /**
     * Unknown URL (e.g. /api/bookz). Since Spring 6.1 this is an exception too.
     * IMPORTANT: without this handler the catch-all below would turn every
     * unknown URL into a 500 - a classic bug!
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "No endpoint found for " + request.getRequestURI(), request);
    }

    // ---------------------------------------------------------------------
    // 4. The safety net: anything we did not expect = a bug on OUR side
    // ---------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        // Log the FULL stack trace for developers (server side only) ...
        log.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), ex);
        // ... but tell the client NOTHING technical: no class names, no stack trace, no SQL.
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong on our side. Please try again later.", request);
    }

    // ---------------------------------------------------------------------

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.of(status.value(), status.getReasonPhrase(), message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
