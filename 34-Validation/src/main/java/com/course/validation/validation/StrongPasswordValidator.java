package com.course.validation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/*
 * The logic behind @StrongPassword.
 *
 * ConstraintValidator<A, T>:
 *   A = the annotation it supports (StrongPassword)
 *   T = the type of value it can check (String)
 *
 * isValid(...) returns true (OK) or false (violation -> the message is reported).
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private int minLength;

    @Override
    public void initialize(StrongPassword annotation) {
        // Called once: read the annotation's attributes here.
        this.minLength = annotation.minLength();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // CONVENTION: null is considered VALID. "Is it present?" is the job of
        // @NotNull / @NotBlank. This keeps each annotation focused on ONE rule.
        if (password == null) {
            return true;
        }
        boolean longEnough = password.length() >= minLength;
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return longEnough && hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
