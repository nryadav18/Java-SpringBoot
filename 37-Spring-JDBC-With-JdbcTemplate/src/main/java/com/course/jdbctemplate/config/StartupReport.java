package com.course.jdbctemplate.config;

import com.course.jdbctemplate.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/*
 * Runs once after startup and prints some facts to the console:
 *   - which DataSource (connection pool) Spring Boot created  -> HikariDataSource
 *   - which StudentRepository implementation is active
 *   - two tiny JdbcTemplate demos: queryForObject and queryForList
 */
@Component
public class StartupReport implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupReport.class);

    private final DataSource dataSource;
    private final StudentRepository repository;
    private final JdbcTemplate jdbcTemplate;

    public StartupReport(DataSource dataSource, StudentRepository repository, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        log.info("DataSource in use       : {}", dataSource.getClass().getSimpleName());
        // @Repository beans are wrapped in a proxy (for exception translation), so we ask
        // ClassUtils for the "real" class name instead of ...$$SpringCGLIB$$0
        log.info("Repository in use       : {}", ClassUtils.getUserClass(repository).getSimpleName());
        log.info("Students in database    : {}", repository.count());

        // queryForObject(sql, Class, args...) -> one value
        String topStudent = jdbcTemplate.queryForObject(
                "SELECT name FROM student ORDER BY marks DESC LIMIT 1", String.class);
        log.info("Top student             : {}", topStudent);

        // queryForList -> List<Map<column, value>>: quick, no RowMapper, but not type-safe
        List<Map<String, Object>> courses = jdbcTemplate.queryForList("SELECT DISTINCT course FROM student ORDER BY course");
        log.info("Courses (queryForList)  : {}", courses);
    }
}
