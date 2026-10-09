package com.course.taskapi.common;

import com.course.taskapi.task.InvalidSortPropertyException;
import com.course.taskapi.task.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * ONE error format for the WHOLE API: ProblemDetail (RFC 9457, "application/problem+json").
 *
 * {
 *   "type": "https://api.example.com/problems/task-not-found",
 *   "title": "Task not found",
 *   "status": 404,
 *   "detail": "Task with id 99 was not found",
 *   "instance": "/api/v1/tasks/99",
 *   "timestamp": "2026-01-01T10:00:00Z"
 * }
 *
 * Extending ResponseEntityExceptionHandler gives us ProblemDetail answers for all
 * of Spring MVC's own errors too (bad JSON, wrong method, type mismatch, 404 URL ...).
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_BASE = "https://api.example.com/problems/";

    // 404 - our own "not found" exception
    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleNotFound(TaskNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create(PROBLEM_BASE + "task-not-found"));
        problem.setTitle("Task not found");
        problem.setProperty("taskId", ex.getTaskId());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 400 - client asked to sort by a field that is not allowed (?sort=colour)
    @ExceptionHandler(InvalidSortPropertyException.class)
    public ProblemDetail handleBadSort(InvalidSortPropertyException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create(PROBLEM_BASE + "invalid-sort"));
        problem.setTitle("Invalid sort property");
        problem.setProperty("allowed", ex.getAllowed());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 400 - @Valid failed. We add a map "errors": { "title": "must not be blank", ... }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "One or more fields are invalid");
        problem.setType(URI.create(PROBLEM_BASE + "validation-error"));
        problem.setTitle("Validation failed");
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.badRequest().body(problem);
    }

    // 500 - anything we did not expect. Log the details, but NEVER send the stack trace.
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong on our side. Please try again later.");
        problem.setTitle("Internal server error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // Add a timestamp to the ProblemDetails that Spring MVC itself creates too.
    @Override
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers,
                                                          HttpStatusCode statusCode, WebRequest request) {
        if (body instanceof ProblemDetail problem && problem.getProperties() == null) {
            problem.setProperty("timestamp", Instant.now());
        }
        return super.createResponseEntity(body, headers, statusCode, request);
    }
}
