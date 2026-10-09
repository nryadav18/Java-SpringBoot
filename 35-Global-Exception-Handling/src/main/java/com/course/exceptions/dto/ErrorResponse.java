package com.course.exceptions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/*
 * ErrorResponse - ONE standard shape for EVERY error our API returns.
 * -------------------------------------------------------------------
 * Front-end and mobile developers love this: they write ONE piece of code
 * to show errors, no matter which endpoint failed.
 *
 * Example:
 * {
 *   "timestamp": "2026-10-09T10:15:30.123Z",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Book not found with id: 99",
 *   "path": "/api/books/99"
 * }
 *
 * fieldErrors is only present for validation errors:
 *   "fieldErrors": [ { "field": "isbn", "message": "ISBN must be exactly 13 digits" } ]
 *
 * @JsonInclude(NON_NULL) = "leave out fields that are null", so normal errors
 * do not carry an empty "fieldErrors": null line.
 *
 * NOTE: Spring also has an interface called org.springframework.web.ErrorResponse.
 * Make sure you import THIS class (com.course.exceptions.dto.ErrorResponse).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDetail> fieldErrors
) {

    /** One invalid field. We deliberately do NOT echo the rejected value (it could be a password). */
    public record FieldErrorDetail(String field, String message) {
    }

    /** Convenience factory for errors without field details. */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, null);
    }
}
