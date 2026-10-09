package com.course.flywaymysql.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * A learning helper: GET /api/db-info tells you
 *   - which profile is active,
 *   - which database you are REALLY connected to (H2 or MySQL),
 *   - which Flyway migrations have been applied.
 *
 * Spring Boot auto-configures both the DataSource and the Flyway bean,
 * so we simply ask for them in the constructor.
 */
@RestController
public class DatabaseInfoController {

    private final DataSource dataSource;
    private final Flyway flyway;
    private final Environment environment;

    public DatabaseInfoController(DataSource dataSource, Flyway flyway, Environment environment) {
        this.dataSource = dataSource;
        this.flyway = flyway;
        this.environment = environment;
    }

    @GetMapping("/api/db-info")
    public Map<String, Object> dbInfo() throws SQLException {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));

        // try-with-resources: the connection goes back to the HikariCP pool afterwards
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            info.put("database", meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            info.put("jdbcUrl", meta.getURL());
        }

        // flyway.info().applied() = the rows of the flyway_schema_history table
        List<String> applied = Arrays.stream(flyway.info().applied())
                .map(DatabaseInfoController::describe)
                .toList();
        info.put("appliedMigrations", applied);
        return info;
    }

    private static String describe(MigrationInfo m) {
        return "V" + m.getVersion() + " - " + m.getDescription() + " (" + m.getState() + ")";
    }
}
