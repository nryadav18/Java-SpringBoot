package com.course.jdbcbasics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Database.java
 * ------------------------------------------------------------
 * What it shows:
 *   - The 3 things you need to connect to ANY database with JDBC:
 *       a JDBC URL, a username, a password
 *   - DriverManager.getConnection(...)  -> gives us a Connection
 *   - Creating the tables (our "schema.sql", written in code) with a plain Statement
 *
 * The URL explained:
 *   jdbc:h2:mem:college;DB_CLOSE_DELAY=-1
 *   |    |  |   |       |
 *   |    |  |   |       +-- keep the in-memory DB alive until the JVM stops
 *   |    |  |   +---------- database name
 *   |    |  +-------------- "mem" = in memory (gone when the program ends)
 *   |    +----------------- which driver: H2
 *   +---------------------- every JDBC URL starts with "jdbc:"
 *
 *   For MySQL it would look like:  jdbc:mysql://localhost:3306/college
 *
 * Not run directly - used by JdbcDemoApp.
 */
public final class Database {

    // In a real app these come from a config file / environment variables, NOT from code.
    public static final String URL = "jdbc:h2:mem:college;DB_CLOSE_DELAY=-1";
    public static final String USER = "sa";
    public static final String PASSWORD = "";

    private Database() {
        // utility class: no objects
    }

    /**
     * Opens a NEW connection every time it is called.
     * Opening connections is slow - real apps use a CONNECTION POOL (HikariCP in Spring Boot).
     * Since JDBC 4 the driver is found automatically on the classpath; no Class.forName(...) needed.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Creates the tables and some starting data. This plays the role of a "schema.sql" file.
     * We use a text block (Topic 15) so the SQL is easy to read.
     */
    public static void createSchema() throws SQLException {
        String schemaSql = """
                DROP TABLE IF EXISTS students;
                CREATE TABLE students (
                    id     INT AUTO_INCREMENT PRIMARY KEY,
                    name   VARCHAR(100) NOT NULL,
                    email  VARCHAR(150) NOT NULL,
                    marks  INT          NOT NULL,
                    CONSTRAINT uk_students_email UNIQUE (email)
                );

                DROP TABLE IF EXISTS accounts;
                CREATE TABLE accounts (
                    id       INT PRIMARY KEY,
                    owner    VARCHAR(100) NOT NULL,
                    balance  DECIMAL(10,2) NOT NULL,
                    CONSTRAINT balance_not_negative CHECK (balance >= 0)
                );
                INSERT INTO accounts (id, owner, balance) VALUES (1, 'Asha', 1000.00);
                INSERT INTO accounts (id, owner, balance) VALUES (2, 'Ben',   500.00);
                """;

        // try-with-resources: Connection and Statement are closed automatically (Topic 10).
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // H2 can run several statements separated by ';' in one execute call.
            stmt.execute(schemaSql);
        }
    }
}
