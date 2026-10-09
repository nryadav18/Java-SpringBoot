package com.course.jdbctemplate.repository;

import com.course.jdbctemplate.model.CourseStats;
import com.course.jdbctemplate.model.Student;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/*
 * Implementation 2: NamedParameterJdbcTemplate with ":name" placeholders.
 * ------------------------------------------------------------------------
 * Same power as JdbcTemplate, but parameters have NAMES instead of positions:
 *
 *   JdbcTemplate:     "... WHERE course = ? AND marks >= ?"      , course, minMarks
 *   Named:            "... WHERE course = :course AND marks >= :minMarks"
 *
 * With 6+ parameters this is MUCH easier to read and you cannot mix up the order.
 *
 * Active when app.repository.type=named
 */
@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "named")
public class NamedParameterStudentRepository implements StudentRepository {

    private static final String SELECT_ALL =
            "SELECT id, name, email, course, marks, enrolled_on FROM student";

    private final NamedParameterJdbcTemplate jdbc;   // also auto-configured by Spring Boot

    public NamedParameterStudentRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Student> findAll() {
        return jdbc.query(SELECT_ALL + " ORDER BY id", StudentRowMapper.INSTANCE);
    }

    @Override
    public Optional<Student> findById(long id) {
        // Alternative to try/catch: query a List and take the first element (0 or 1 rows)
        List<Student> result = jdbc.query(SELECT_ALL + " WHERE id = :id",
                Map.of("id", id), StudentRowMapper.INSTANCE);
        return result.stream().findFirst();
    }

    @Override
    public List<Student> findByCourse(String course) {
        return jdbc.query(SELECT_ALL + " WHERE LOWER(course) = LOWER(:course) ORDER BY marks DESC",
                Map.of("course", course), StudentRowMapper.INSTANCE);
    }

    @Override
    public Student save(Student s) {
        String sql = """
                INSERT INTO student (name, email, course, marks, enrolled_on)
                VALUES (:name, :email, :course, :marks, :enrolledOn)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, toParams(s), keyHolder, new String[]{"id"});
        return s.withId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public boolean update(Student s) {
        String sql = """
                UPDATE student
                SET name = :name, email = :email, course = :course, marks = :marks, enrolled_on = :enrolledOn
                WHERE id = :id
                """;
        return jdbc.update(sql, toParams(s)) == 1;
    }

    @Override
    public boolean deleteById(long id) {
        return jdbc.update("DELETE FROM student WHERE id = :id", Map.of("id", id)) == 1;
    }

    @Override
    public long count() {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM student", Map.of(), Long.class);
        return count == null ? 0 : count;
    }

    @Override
    public List<CourseStats> courseStatistics() {
        return jdbc.query("""
                        SELECT course, COUNT(*) AS students, AVG(CAST(marks AS DOUBLE)) AS avg_marks
                        FROM student GROUP BY course ORDER BY course
                        """,
                (rs, rowNum) -> new CourseStats(rs.getString("course"), rs.getLong("students"),
                        Math.round(rs.getDouble("avg_marks") * 10) / 10.0));
    }

    /** MapSqlParameterSource = a Map of name -> value, with a fluent addValue(...) API. */
    private SqlParameterSource toParams(Student s) {
        return new MapSqlParameterSource()
                .addValue("id", s.id())
                .addValue("name", s.name())
                .addValue("email", s.email())
                .addValue("course", s.course())
                .addValue("marks", s.marks())
                .addValue("enrolledOn", s.enrolledOn());
    }
}
