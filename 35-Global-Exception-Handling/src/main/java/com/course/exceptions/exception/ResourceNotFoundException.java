package com.course.exceptions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * ResourceNotFoundException - a REUSABLE "404" exception for any kind of resource.
 * ---------------------------------------------------------------------------------
 *   throw new ResourceNotFoundException("Book", "id", 99);
 *   -> message: "Book not found with id: 99"
 *
 * It extends RuntimeException (UNCHECKED, topic 10) so service methods do not
 * need "throws" clauses everywhere.
 *
 * @ResponseStatus(NOT_FOUND):
 *   used ONLY when no @ExceptionHandler handles this exception (e.g. in the
 *   "plain" profile). When our @RestControllerAdvice is active, the handler wins.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(resourceName + " not found with " + fieldName + ": " + fieldValue);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() { return resourceName; }
    public String getFieldName() { return fieldName; }
    public Object getFieldValue() { return fieldValue; }
}
