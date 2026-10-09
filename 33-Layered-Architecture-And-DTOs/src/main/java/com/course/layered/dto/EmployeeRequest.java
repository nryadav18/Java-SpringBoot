package com.course.layered.dto;

import java.math.BigDecimal;

/*
 * EmployeeRequest = the INPUT DTO (Data Transfer Object).
 * --------------------------------------------------------
 * This is the exact shape of JSON a client may SEND to create or update
 * an employee. Notice what is NOT here:
 *   - no "id"            -> the server decides ids, clients must not choose them
 *   - no "createdAt"     -> the server sets it
 *   - no "internalNotes" -> only HR tools may set it, not this public API
 *
 * A Java "record" is perfect for DTOs: immutable, tiny, and Jackson can
 * read and write it out of the box.
 *
 * Example JSON:
 * { "firstName":"Asha", "lastName":"Rao", "email":"asha@company.com",
 *   "department":"Engineering", "salary":85000 }
 *
 * (In topic 34 we add validation annotations like @NotBlank here.)
 */
public record EmployeeRequest(
        String firstName,
        String lastName,
        String email,
        String department,
        BigDecimal salary
) {
}
