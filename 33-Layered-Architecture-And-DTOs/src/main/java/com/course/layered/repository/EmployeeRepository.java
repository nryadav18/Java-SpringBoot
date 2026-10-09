package com.course.layered.repository;

import com.course.layered.model.Employee;

import java.util.List;
import java.util.Optional;

/*
 * EmployeeRepository - the CONTRACT for storing employees.
 * --------------------------------------------------------
 * The service depends on THIS INTERFACE, never on a concrete class.
 * Today the implementation keeps data in memory (InMemoryEmployeeRepository).
 * In topics 37/38 we can write a JDBC or JPA implementation and the service
 * will not change a single line. That is "programming to an interface"
 * (topic 8) paying off.
 *
 * The method names deliberately look like Spring Data JPA's
 * (save, findById, findAll, deleteById, existsById) so the later switch is easy.
 */
public interface EmployeeRepository {

    Employee save(Employee employee);          // insert (no id yet) or update (has id)

    Optional<Employee> findById(Long id);      // Optional = "maybe there is one" (topic 14)

    List<Employee> findAll();

    List<Employee> findByDepartment(String department);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    void deleteById(Long id);
}
