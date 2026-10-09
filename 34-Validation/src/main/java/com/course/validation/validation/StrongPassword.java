package com.course.validation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @StrongPassword - a CUSTOM constraint annotation.
 * --------------------------------------------------
 * Rule: at least 8 characters, with an uppercase letter, a lowercase letter,
 * a digit and a special character.
 *
 * A custom constraint needs TWO pieces:
 *   1. this annotation (with @Constraint pointing to the validator class)
 *   2. the validator class that contains the actual check (StrongPasswordValidator)
 *
 * Every constraint annotation MUST have these three attributes:
 *   message() - default error message
 *   groups()  - for validation groups
 *   payload() - extra metadata (rarely used, but required by the spec)
 *
 * @Retention(RUNTIME) - the annotation must still exist when the app runs (topic 18).
 * @Target - where it may be placed: fields, record components, parameters ...
 */
@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT,
        ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default "Password must be at least {minLength} characters and contain "
            + "an uppercase letter, a lowercase letter, a digit and a special character";

    // Our own extra attribute - it can be used in the message as {minLength}
    int minLength() default 8;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
