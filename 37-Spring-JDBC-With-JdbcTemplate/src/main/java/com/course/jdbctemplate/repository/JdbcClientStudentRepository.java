package com.course.jdbctemplate.repository;

import com.course.jdbctemplate.model.CourseStats;
import com.course.jdbctemplate.model.Student;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
 * Implementation 3: JdbcClient - the modern FLUENT API (Spring Framework 6.1 / Boot 3.2+).
 * ----------------------------------------------------------------------------------------
 *   jdbcClient.sql("SELECT ... WHERE id = :id")   // 1. the SQL
 *             .param("id", id)                    // 2. the parameters (named or positional)
 *             .query(Student.class)               // 3. how to map rows
 *             .optional();                        // 4. what you want back: list(), single(), optional()
 *
 * It reads like a sentence. Under the hood it uses NamedParameterJdbcTemplate/JdbcTemplate,
 * so it has the same features and the same safety.
 *
 * query(Student.class) maps columns to record components AUTOMATICALLY
 * (enrolled_on -> enrolledOn: snake_case is matched to camelCase). No RowMapper needed!
 *
 * Active when app.repository.type=jdbc-client
 */
@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "jdbc-client")
public class JdbcClientStudentRepository implements StudentRepository {

    private static final String SELECT_ALL =
            "SELECT id, name, email, course, marks, enrolled_on FROM student";

    private final JdbcClient jdbcClient;   // auto-configured by Spring Boot 3.2+

    public JdbcClientStudentRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Student> findAll() {
        return jdbcClient.sql(SELECT_ALL + " ORDER BY id")
                .query(Student.class)          // automatic mapping to the record
                .list();
    }

    @Override
    public Optional<Student> findById(long id) {
        return jdbcClient.sql(SELECT_ALL + " WHERE id = :id")
                .param("id", id)
                .query(Student.class)
                .optional();                   // 0 rows -> Optional.empty(), no exception
    }

    @Override
    public List<Student> findByCourse(String course) {
        return jdbcClient.sql(SELECT_ALL + " WHERE LOWER(course) = LOWER(?) ORDER BY marks DESC")
                .param(course)                 // positional "?" parameters work too
                .query(StudentRowMapper.INSTANCE)   // ...and you can still use your own RowMapper
                .list();
    }

    @Override
    public Student save(Student s) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                        INSERT INTO student (name, email, course, marks, enrolled_on)
                        VALUES (:name, :email, :course, :marks, :enrolledOn)
                        """)
                .param("name", s.name())
                .param("email", s.email())
                .param("course", s.course())
                .param("marks", s.marks())
                .param("enrolledOn", s.enrolledOn())
                .update(keyHolder, "id");      // fill keyHolder with the generated "id"
        return s.withId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public boolean update(Student s) {
        int rows = jdbcClient.sql("""
                        UPDATE student
                        SET name = :name, email = :email, course = :course, marks = :marks, enrolled_on = :enrolledOn
                        WHERE id = :id
                        """)
                // paramSource(object) reads ALL named parameters from the record's accessors!
                .paramSource(s)
                .update();
        return rows == 1;
    }

    @Override
    public boolean deleteById(long id) {
        return jdbcClient.sql("DELETE FROM student WHERE id = :id").param("id", id).update() == 1;
    }

    @Override
    public long count() {
        return jdbcClient.sql("SELECT COUNT(*) FROM student")
                .query(Long.class)             // single column -> simple type
                .single();
    }

    @Override
    public List<CourseStats> courseStatistics() {
        return jdbcClient.sql("""
                        SELECT course, COUNT(*) AS students, AVG(CAST(marks AS DOUBLE)) AS avg_marks
                        FROM student GROUP BY course ORDER BY course
                        """)
                .query((rs, rowNum) -> new CourseStats(rs.getString("course"), rs.getLong("students"),
                        Math.round(rs.getDouble("avg_marks") * 10) / 10.0))
                .list();
    }
}
