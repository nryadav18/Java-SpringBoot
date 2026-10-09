package com.course.layered.service;

import com.course.layered.dto.DepartmentSummary;
import com.course.layered.dto.EmployeeRequest;
import com.course.layered.dto.EmployeeResponse;
import com.course.layered.exception.DuplicateEmailException;
import com.course.layered.exception.EmployeeNotFoundException;
import com.course.layered.mapper.EmployeeMapper;
import com.course.layered.model.Employee;
import com.course.layered.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/*
 * EmployeeService - the BUSINESS LOGIC layer (the "brain").
 * ---------------------------------------------------------
 * Responsibilities:
 *   - business rules  (email must be unique, employee must exist, ...)
 *   - coordinating repositories and mappers
 *   - later: transactions (@Transactional, topic 42)
 *
 * It speaks DTOs with the controller (EmployeeRequest in, EmployeeResponse out)
 * and domain objects (Employee) with the repository.
 * It does NOT know about HTTP: no ResponseEntity, no status codes here.
 *
 * Constructor injection (topic 24): Spring sees ONE constructor and passes in
 * the EmployeeRepository bean and the EmployeeMapper bean automatically.
 * Fields are final -> they can never be null or swapped later.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository repository;   // the INTERFACE, not InMemory...
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EmployeeResponse> findAll(String department) {
        List<Employee> employees = (department == null || department.isBlank())
                ? repository.findAll()
                : repository.findByDepartment(department);
        return employees.stream()
                .map(mapper::toResponse)      // Employee -> EmployeeResponse
                .toList();
    }

    public EmployeeResponse findById(Long id) {
        return mapper.toResponse(getExisting(id));
    }

    public EmployeeResponse create(EmployeeRequest request) {
        // Business rule: emails are unique across the company.
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        Employee saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee existing = getExisting(id);
        // If the email changes, the new one must not belong to someone else.
        boolean emailChanged = !existing.getEmail().equalsIgnoreCase(request.email());
        if (emailChanged && repository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        mapper.updateEntity(existing, request);
        return mapper.toResponse(repository.save(existing));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /** A "report" use case: how many employees per department (sorted by name). */
    public List<DepartmentSummary> departmentSummary() {
        Map<String, Long> counts = repository.findAll().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.counting()));
        return counts.entrySet().stream()
                .map(entry -> new DepartmentSummary(entry.getKey(), entry.getValue()))
                .toList();
    }

    // Small private helper: load the employee or fail. Reused by findById and update.
    private Employee getExisting(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}
