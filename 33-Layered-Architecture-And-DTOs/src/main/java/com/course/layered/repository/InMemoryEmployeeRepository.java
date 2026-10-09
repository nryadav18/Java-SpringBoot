package com.course.layered.repository;

import com.course.layered.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
 * InMemoryEmployeeRepository - stores employees in a Map instead of a database.
 * ------------------------------------------------------------------------------
 * Responsibilities of the REPOSITORY layer:
 *   - save / load / delete data. Nothing else.
 *   - NO business rules (like "email must be unique") -> that is the service's job.
 *   - NO HTTP knowledge (status codes, JSON)          -> that is the controller's job.
 *
 * @Repository = a @Component for data-access classes (topic 23).
 *
 * Why ConcurrentHashMap and AtomicLong? A web server handles many requests
 * at the same time on different threads (topic 17). These classes are
 * thread-safe; a plain HashMap and a plain long counter are not.
 *
 * NOTE: data is lost when the app stops - that is fine for learning.
 */
@Repository
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<Long, Employee> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);   // like a DB auto-increment

    @Override
    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(idSequence.incrementAndGet());     // new employee -> new id
        }
        storage.put(employee.getId(), employee);              // insert or replace
        return employee;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> all = new ArrayList<>(storage.values());
        all.sort(Comparator.comparing(Employee::getId));      // stable, predictable order
        return all;
    }

    @Override
    public List<Employee> findByDepartment(String department) {
        return findAll().stream()
                .filter(e -> e.getDepartment() != null
                        && e.getDepartment().equalsIgnoreCase(department))
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return storage.values().stream()
                .anyMatch(e -> e.getEmail() != null && e.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
