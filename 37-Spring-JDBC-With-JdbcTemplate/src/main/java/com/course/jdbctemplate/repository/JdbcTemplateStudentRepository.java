package com.course.jdbctemplate.repository;

import com.course.jdbctemplate.model.CourseStats;
import com.course.jdbctemplate.model.Student;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
 * Implementation 1: classic JdbcTemplate with "?" placeholders.
 * --------------------------------------------------------------
 * JdbcTemplate does all the boring JDBC work (topic 19) for us:
 *   - gets a Connection from the pool and gives it back
 *   - creates a PreparedStatement and sets the parameters (safe from SQL injection)
 *   - loops over the ResultSet and calls our RowMapper
 *   - closes everything, even when an exception happens
 *   - translates SQLException into Spring's DataAccessException hierarchy
 *
 * Active when app.repository.type=jdbc-template, or when the property is missing.
 */
@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "jdbc-template", matchIfMissing = true)
public class JdbcTemplateStudentRepository implements StudentRepository {

    private static final String SELECT_ALL =
            "SELECT id, name, email, course, marks, enrolled_on FROM student";

    private final JdbcTemplate jdbcTemplate;   // auto-configured by Spring Boot

    public JdbcTemplateStudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Student> findAll() {
        // query(sql, rowMapper) -> List: one Student per row
        return jdbcTemplate.query(SELECT_ALL + " ORDER BY id", StudentRowMapper.INSTANCE);
    }

    @Override
    public Optional<Student> findById(long id) {
        try {
            // queryForObject expects EXACTLY one row
            Student student = jdbcTemplate.queryForObject(
                    SELECT_ALL + " WHERE id = ?", StudentRowMapper.INSTANCE, id);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            // 0 rows -> queryForObject throws. Convert it to an empty Optional.
            return Optional.empty();
        }
    }

    @Override
    public List<Student> findByCourse(String course) {
        // Each "?" is filled by the extra arguments, IN ORDER.
        return jdbcTemplate.query(SELECT_ALL + " WHERE LOWER(course) = LOWER(?) ORDER BY marks DESC",
                StudentRowMapper.INSTANCE, course);
    }

    @Override
    public Student save(Student s) {
        String sql = "INSERT INTO student (name, email, course, marks, enrolled_on) VALUES (?, ?, ?, ?, ?)";

        // KeyHolder receives the id that the DATABASE generated for the new row.
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            // The second argument says: "please give me back the generated 'id' column"
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, s.name());
            ps.setString(2, s.email());
            ps.setString(3, s.course());
            ps.setInt(4, s.marks());
            ps.setDate(5, Date.valueOf(s.enrolledOn()));
            return ps;
        }, keyHolder);

        long newId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return s.withId(newId);
    }

    @Override
    public boolean update(Student s) {
        // update(...) is used for INSERT, UPDATE and DELETE. It returns the number of affected rows.
        int rows = jdbcTemplate.update(
                "UPDATE student SET name = ?, email = ?, course = ?, marks = ?, enrolled_on = ? WHERE id = ?",
                s.name(), s.email(), s.course(), s.marks(), s.enrolledOn(), s.id());
        return rows == 1;
    }

    @Override
    public boolean deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM student WHERE id = ?", id) == 1;
    }

    @Override
    public long count() {
        // queryForObject with a simple type: one row, one column -> Long
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM student", Long.class);
        return count == null ? 0 : count;
    }

    @Override
    public List<CourseStats> courseStatistics() {
        // A RowMapper can also be a LAMBDA: (resultSet, rowNumber) -> object
        return jdbcTemplate.query("""
                        SELECT course, COUNT(*) AS students, AVG(CAST(marks AS DOUBLE)) AS avg_marks
                        FROM student
                        GROUP BY course
                        ORDER BY course
                        """,
                (rs, rowNum) -> new CourseStats(
                        rs.getString("course"),
                        rs.getLong("students"),
                        Math.round(rs.getDouble("avg_marks") * 10) / 10.0));
    }
}
