package com.course.layered.mapper;

import com.course.layered.dto.EmployeeRequest;
import com.course.layered.dto.EmployeeResponse;
import com.course.layered.model.Employee;
import org.springframework.stereotype.Component;

/*
 * EmployeeMapper - converts between DTOs and the domain model.
 * ------------------------------------------------------------
 * Keeping ALL conversion code in one class means:
 *   - the controller stays thin (no field-copying code there)
 *   - the service focuses on business rules
 *   - when a field is added, there is exactly ONE place to update
 *
 * This is a "manual mapper". Libraries such as MapStruct can GENERATE this
 * code at compile time from an interface - see the lesson for an example.
 *
 * @Component makes it a Spring bean so it can be injected into the service.
 */
@Component
public class EmployeeMapper {

    /** Request DTO -> new domain object (used when CREATING). */
    public Employee toEntity(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setDepartment(request.department());
        employee.setSalary(request.salary());
        // id and createdAt are NOT copied: the server controls them.
        return employee;
    }

    /** Copy new values from a request onto an EXISTING employee (used when UPDATING). */
    public void updateEntity(Employee existing, EmployeeRequest request) {
        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setEmail(request.email());
        existing.setDepartment(request.department());
        existing.setSalary(request.salary());
        // id, createdAt and internalNotes stay untouched.
    }

    /** Domain object -> response DTO (used for EVERY response). */
    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment()
        );
    }
}
