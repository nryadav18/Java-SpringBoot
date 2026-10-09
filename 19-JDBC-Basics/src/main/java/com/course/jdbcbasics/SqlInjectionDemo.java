package com.course.jdbcbasics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * SqlInjectionDemo.java
 * ------------------------------------------------------------
 * What it shows:
 *   WHY you must never build SQL by gluing strings together.
 *
 *   A "search student by email" feature receives text typed by a user.
 *   A hacker types:     ' OR '1'='1
 *
 *   - With Statement + string concatenation the SQL becomes:
 *       SELECT ... WHERE email = '' OR '1'='1'
 *     '1'='1' is always true -> the query returns EVERY student. Data leak!
 *
 *   - With PreparedStatement the input is sent as DATA, never as SQL code,
 *     so the database looks for a student whose email is literally "' OR '1'='1"
 *     and finds nobody. Safe.
 *
 * Not run directly - called from JdbcDemoApp.
 */
public class SqlInjectionDemo {

    /** DANGEROUS - for teaching only. Never write code like this. */
    public int countMatchesUnsafe(String emailFromUser) throws SQLException {
        String sql = "SELECT id, name FROM students WHERE email = '" + emailFromUser + "'";
        System.out.println("  Unsafe SQL sent to DB : " + sql);

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                count++;
            }
            return count;
        }
    }

    /** SAFE - the value is bound to the ? placeholder. */
    public int countMatchesSafe(String emailFromUser) throws SQLException {
        String sql = "SELECT id, name FROM students WHERE email = ?";
        System.out.println("  Safe SQL sent to DB   : " + sql + "   with parameter [" + emailFromUser + "]");

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emailFromUser);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                }
                return count;
            }
        }
    }
}
