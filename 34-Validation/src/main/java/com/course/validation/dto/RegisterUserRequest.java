package com.course.validation.dto;

import com.course.validation.validation.StrongPassword;
import com.course.validation.validation.ValidPhoneNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/*
 * RegisterUserRequest - the JSON a client sends to POST /api/users.
 * ------------------------------------------------------------------
 * Every constraint annotation is a RULE. Hibernate Validator checks all
 * of them when the controller parameter is marked with @Valid.
 *
 * On a record, put the annotations directly on the components (the
 * parameters below). Java copies them to the fields and accessors.
 *
 * The "message" attribute is what the client will see. {min} and {max}
 * are placeholders filled from the annotation's own attributes.
 */
public record RegisterUserRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between {min} and {max} characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        // Our OWN annotation - see validation/StrongPassword.java
        @NotBlank(message = "Password is required")
        @StrongPassword
        String password,

        // Our OWN annotation - optional field: null is allowed, but if present it must be valid
        @ValidPhoneNumber
        String phone,

        @NotNull(message = "Age is required")
        @Min(value = 18, message = "You must be at least {value} years old")
        @Max(value = 120, message = "Age must be at most {value}")
        Integer age,                       // Integer (not int) so "missing" becomes null, not 0

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,             // JSON: "2000-05-21"

        // NESTED validation: @Valid tells the validator to "go inside" the address
        // and check ITS annotations too. Without @Valid only @NotNull is checked.
        @NotNull(message = "Address is required")
        @Valid
        AddressRequest address,

        // Constraints on the list AND on every element inside it (type-use annotation)
        @Size(max = 5, message = "You can choose at most {max} interests")
        List<@NotBlank(message = "Interest must not be blank") String> interests
) {
}
