package com.course.validation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @ValidPhoneNumber - custom constraint for an Indian mobile number:
 *   10 digits starting with 6, 7, 8 or 9, optionally prefixed with +91.
 *   Valid:   9876543210   +919876543210
 *   Invalid: 12345   0987654321   98765-43210
 *
 * Spaces are ignored, so "98765 43210" is accepted.
 */
@Documented
@Constraint(validatedBy = ValidPhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT,
        ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {

    String message() default "Phone number must be a valid 10-digit mobile number (optionally starting with +91)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
