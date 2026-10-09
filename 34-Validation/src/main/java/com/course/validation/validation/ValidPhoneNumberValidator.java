package com.course.validation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/*
 * The logic behind @ValidPhoneNumber.
 * A precompiled regex Pattern is faster than calling String.matches(...) every time.
 */
public class ValidPhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    // (\+91)?   optional country code
    // [6-9]     first digit 6, 7, 8 or 9
    // \d{9}     then exactly nine more digits
    private static final Pattern INDIAN_MOBILE = Pattern.compile("^(\\+91)?[6-9]\\d{9}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isBlank()) {
            return true;   // optional field: absence is fine (add @NotBlank to make it required)
        }
        String withoutSpaces = phone.replace(" ", "");
        return INDIAN_MOBILE.matcher(withoutSpaces).matches();
    }
}
