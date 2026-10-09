package com.course.layered.dto;

/*
 * EmployeeResponse = the OUTPUT DTO.
 * -----------------------------------
 * This is the exact shape of JSON the API RETURNS. WE choose what the
 * outside world may see:
 *   - fullName is a "computed" field (firstName + " " + lastName)
 *   - salary and internalNotes are deliberately left out
 *
 * If tomorrow we rename a field inside Employee, this record stays the same,
 * so mobile apps and other teams that use our API do not break.
 */
public record EmployeeResponse(
        Long id,
        String fullName,
        String email,
        String department
) {
}
