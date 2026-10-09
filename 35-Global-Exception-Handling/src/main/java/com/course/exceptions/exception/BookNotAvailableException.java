package com.course.exceptions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * A BUSINESS RULE failure: the request is well-formed, the book exists,
 * but no copy is available right now.
 *
 * 422 Unprocessable Entity (some teams use 409 Conflict) = "I understood you,
 * but I cannot do this because of a business rule".
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(String title) {
        super("No copies of '" + title + "' are available right now");
    }
}
