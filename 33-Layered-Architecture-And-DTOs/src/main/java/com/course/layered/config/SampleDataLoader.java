package com.course.layered.config;

import com.course.layered.dto.EmployeeRequest;
import com.course.layered.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/*
 * Loads a few sample employees at startup so the API is not empty.
 *
 * Notice: it goes THROUGH THE SERVICE (not straight into the repository),
 * so the same business rules (unique email) apply to sample data too.
 *
 * CommandLineRunner.run(...) is called once, right after the app has started (topic 28).
 */
@Component
public class SampleDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SampleDataLoader.class);

    private final EmployeeService employeeService;

    public SampleDataLoader(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void run(String... args) {
        employeeService.create(new EmployeeRequest("Asha", "Rao", "asha.rao@company.com",
                "Engineering", new BigDecimal("85000")));
        employeeService.create(new EmployeeRequest("Ravi", "Kumar", "ravi.kumar@company.com",
                "Engineering", new BigDecimal("78000")));
        employeeService.create(new EmployeeRequest("Meera", "Shah", "meera.shah@company.com",
                "HR", new BigDecimal("65000")));
        log.info("Loaded {} sample employees", employeeService.findAll(null).size());
    }
}
