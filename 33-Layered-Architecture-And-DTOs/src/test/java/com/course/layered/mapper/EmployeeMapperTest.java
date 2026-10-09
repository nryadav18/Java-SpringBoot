package com.course.layered.mapper;

import com.course.layered.dto.EmployeeRequest;
import com.course.layered.dto.EmployeeResponse;
import com.course.layered.model.Employee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Mapper tests are tiny but valuable: they catch "forgot to copy a field" bugs.
 */
class EmployeeMapperTest {

    private final EmployeeMapper mapper = new EmployeeMapper();

    @Test
    void toEntityCopiesAllRequestFieldsButNoId() {
        EmployeeRequest request = new EmployeeRequest("Ravi", "Kumar", "ravi@company.com",
                "Engineering", new BigDecimal("78000"));

        Employee employee = mapper.toEntity(request);

        assertThat(employee.getId()).isNull();          // the repository assigns ids
        assertThat(employee.getSalary()).isEqualByComparingTo("78000");
        assertThat(employee.getCreatedAt()).isNotNull();
    }

    @Test
    void toResponseHidesSalaryAndBuildsFullName() {
        Employee employee = new Employee();
        employee.setId(7L);
        employee.setFirstName("Ravi");
        employee.setLastName("Kumar");
        employee.setEmail("ravi@company.com");
        employee.setDepartment("Engineering");
        employee.setSalary(new BigDecimal("78000"));

        EmployeeResponse response = mapper.toResponse(employee);

        assertThat(response.fullName()).isEqualTo("Ravi Kumar");
        // EmployeeResponse simply has no salary component - the compiler guarantees it is hidden.
        assertThat(response.toString()).doesNotContain("78000");
    }
}
