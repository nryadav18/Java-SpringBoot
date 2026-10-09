package com.course.layered.controller;

import com.course.layered.dto.DepartmentSummary;
import com.course.layered.dto.EmployeeRequest;
import com.course.layered.dto.EmployeeResponse;
import com.course.layered.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/*
 * EmployeeController - the WEB layer (the "front desk").
 * -------------------------------------------------------
 * Responsibilities:
 *   - map URLs + HTTP methods to Java methods
 *   - read input (path variables, query params, JSON body -> DTO)
 *   - call ONE service method
 *   - choose the HTTP status code and return a DTO (-> JSON)
 *
 * It contains NO business rules and NEVER touches the repository directly.
 * Every method here is 1-3 lines: that is what a "thin controller" looks like.
 *
 * Endpoints:
 *   GET    /api/employees                       list all (optional ?department=)
 *   GET    /api/employees/{id}                  get one
 *   POST   /api/employees                       create  -> 201 + Location header
 *   PUT    /api/employees/{id}                  update
 *   DELETE /api/employees/{id}                  delete  -> 204
 *   GET    /api/employees/departments/summary   count per department
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeResponse> getAll(@RequestParam(required = false) String department) {
        return employeeService.findAll(department);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getOne(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@RequestBody EmployeeRequest request) {
        EmployeeResponse created = employeeService.create(request);
        // Build "http://localhost:8080/api/employees/{newId}" for the Location header (topic 32)
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @RequestBody EmployeeRequest request) {
        return employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/departments/summary")
    public List<DepartmentSummary> departmentSummary() {
        return employeeService.departmentSummary();
    }
}
