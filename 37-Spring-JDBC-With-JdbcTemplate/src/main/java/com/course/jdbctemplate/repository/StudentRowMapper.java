package com.course.jdbctemplate.repository;

import com.course.jdbctemplate.model.Student;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * StudentRowMapper - converts ONE row of a ResultSet into ONE Student.
 * ---------------------------------------------------------------------
 * JdbcTemplate does the loop for us ("while (rs.next())") and calls mapRow
 * once per row. We only describe how a single row becomes an object.
 *
 * Compare with topic 19, where we wrote the loop, created the list, and
 * closed the ResultSet ourselves.
 *
 * It is stateless, so one shared instance is safe to reuse everywhere.
 */
public class StudentRowMapper implements RowMapper<Student> {

    public static final StudentRowMapper INSTANCE = new StudentRowMapper();

    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Student(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("course"),
                rs.getInt("marks"),
                rs.getObject("enrolled_on", java.time.LocalDate.class)  // DATE -> LocalDate
        );
    }
}
