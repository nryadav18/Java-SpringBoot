package com.course.layered.service;

import com.course.layered.dto.EmployeeRequest;
import com.course.layered.dto.EmployeeResponse;
import com.course.layered.exception.DuplicateEmailException;
import com.course.layered.exception.EmployeeNotFoundException;
import com.course.layered.mapper.EmployeeMapper;
import com.course.layered.repository.InMemoryEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
 * A plain unit test of the SERVICE layer - no Spring, no web server.
 *
 * Because the layers are separated and use constructor injection, we can
 * simply "new" the service with a real in-memory repository and mapper.
 * That is one of the biggest benefits of a layered design: easy testing.
 *
 * Run:  mvn test
 */
class EmployeeServiceTest {

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        // A fresh repository for every test -> tests never affect each other.
        service = new EmployeeService(new InMemoryEmployeeRepository(), new EmployeeMapper());
    }

    @Test
    void createReturnsResponseWithIdAndFullName() {
        EmployeeResponse created = service.create(request("asha@company.com"));

        assertThat(created.id()).isEqualTo(1L);
        assertThat(created.fullName()).isEqualTo("Asha Rao");
        assertThat(created.department()).isEqualTo("Engineering");
    }

    @Test
    void createRejectsDuplicateEmail() {
        service.create(request("asha@company.com"));

        assertThatThrownBy(() -> service.create(request("ASHA@company.com")))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void filterByDepartmentIgnoresCase() {
        service.create(request("asha@company.com"));
        service.create(new EmployeeRequest("Meera", "Shah", "meera@company.com", "HR",
                new BigDecimal("65000")));

        assertThat(service.findAll("engineering")).hasSize(1);
        assertThat(service.findAll(null)).hasSize(2);
    }

    private EmployeeRequest request(String email) {
        return new EmployeeRequest("Asha", "Rao", email, "Engineering", new BigDecimal("85000"));
    }
}
