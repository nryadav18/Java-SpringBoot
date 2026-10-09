package com.course.jdbctemplate.repository;

import com.course.jdbctemplate.model.CourseStats;
import com.course.jdbctemplate.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
 * ONE set of tests that EVERY StudentRepository implementation must pass
 * (a "contract test"). The three subclasses below run it against the
 * JdbcTemplate, NamedParameterJdbcTemplate and JdbcClient versions.
 *
 * The subclasses use @JdbcTest - a "test slice" that starts ONLY the database
 * part of Spring Boot (DataSource, JdbcTemplate, JdbcClient, schema.sql/data.sql),
 * not the web server. Each test runs in a transaction that is ROLLED BACK at the
 * end, so every test starts with the same 5 students from data.sql.
 *
 * Gotcha: Spring looks for @Transactional on the class that DECLARES the test
 * method. Our @Test methods are declared here, in the abstract class, so we
 * repeat @Transactional here - otherwise changes would NOT be rolled back and
 * tests would affect each other.
 */
@Transactional   // see the comment above: needed because the @Test methods live in THIS (abstract) class
abstract class StudentRepositoryContractTest {

    @Autowired
    protected StudentRepository repository;

    @Test
    void findAllReturnsSampleData() {
        assertThat(repository.findAll()).hasSize(5);
        assertThat(repository.count()).isEqualTo(5);
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        assertThat(repository.findById(1)).get().extracting(Student::name).isEqualTo("Asha Rao");
        assertThat(repository.findById(999)).isEmpty();
    }

    @Test
    void findByCourseIgnoresCaseAndSortsByMarks() {
        List<Student> java = repository.findByCourse("JAVA");
        assertThat(java).extracting(Student::name).containsExactly("Asha Rao", "Ravi Kumar");
    }

    @Test
    void saveReturnsGeneratedId() {
        Student saved = repository.save(new Student(null, "Kiran", "kiran@college.edu", "SQL", 70,
                LocalDate.of(2026, 7, 1)));
        assertThat(saved.id()).isNotNull().isGreaterThan(5L);
        assertThat(repository.findById(saved.id())).isPresent();
    }

    @Test
    void duplicateEmailIsTranslatedToDuplicateKeyException() {
        Student duplicate = new Student(null, "Copy", "asha@college.edu", "Java", 50, LocalDate.of(2026, 7, 1));
        assertThatThrownBy(() -> repository.save(duplicate)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void updateAndDeleteReportWhetherARowWasAffected() {
        Student asha = repository.findById(1).orElseThrow();
        assertThat(repository.update(new Student(1L, asha.name(), asha.email(), "Spring", 95, asha.enrolledOn())))
                .isTrue();
        assertThat(repository.findById(1).orElseThrow().marks()).isEqualTo(95);

        assertThat(repository.update(new Student(999L, "x", "x@x.com", "x", 1, LocalDate.now()))).isFalse();
        assertThat(repository.deleteById(2)).isTrue();
        assertThat(repository.deleteById(2)).isFalse();
    }

    @Test
    void statisticsGroupByCourse() {
        assertThat(repository.courseStatistics())
                .contains(new CourseStats("Java", 2, 85.0), new CourseStats("SQL", 1, 88.0));
    }
}
