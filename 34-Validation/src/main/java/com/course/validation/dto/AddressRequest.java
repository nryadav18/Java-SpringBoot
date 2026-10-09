package com.course.validation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/*
 * AddressRequest - a NESTED DTO inside RegisterUserRequest.
 * Its rules are only checked because the parent field is marked @Valid.
 *
 * Error field names become "address.city", "address.pinCode" ...
 */
public record AddressRequest(

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "City is required")
        String city,

        // @Pattern = "must match this regular expression". Here: exactly 6 digits (Indian PIN code).
        @NotBlank(message = "PIN code is required")
        @Pattern(regexp = "\\d{6}", message = "PIN code must be exactly 6 digits")
        String pinCode
) {
}
