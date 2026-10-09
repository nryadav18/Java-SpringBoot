package com.course.layered.dto;

/*
 * A small "report" DTO. It does not match any stored object at all.
 * DTOs are shaped for the CLIENT's needs, not for the database.
 * Example JSON: { "department":"Engineering", "employeeCount":2 }
 */
public record DepartmentSummary(String department, long employeeCount) {
}
