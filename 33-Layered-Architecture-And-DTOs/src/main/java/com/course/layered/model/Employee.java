package com.course.layered.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Employee = our DOMAIN MODEL (in topic 38 it becomes a JPA @Entity).
 * -------------------------------------------------------------------
 * This is the "internal" object that the service and repository work with.
 * It holds EVERYTHING we store about an employee - including data that must
 * NEVER be sent to API clients (salary, internal HR notes).
 *
 * That is exactly why the controller never returns this class directly.
 * Instead it returns an EmployeeResponse DTO (see the dto package).
 *
 * It is a normal mutable class (not a record) because entities change over
 * time (e.g. an employee moves to another department) and JPA (topic 38)
 * needs a no-arg constructor and setters.
 */
public class Employee {

    private Long id;                 // given by the repository when saved
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private BigDecimal salary;       // SENSITIVE - never exposed in responses
    private String internalNotes;    // SENSITIVE - HR only
    private LocalDateTime createdAt; // set once, when the employee is created

    public Employee() {
        // empty constructor - frameworks like JPA need it
        this.createdAt = LocalDateTime.now();
    }

    // ----- getters and setters (this is the code Lombok would generate for us) -----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        // Careful: we do NOT print salary or notes, so they never end up in log files.
        return "Employee{id=" + id + ", name=" + firstName + " " + lastName
                + ", department=" + department + "}";
    }
}
