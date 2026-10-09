package com.course.jdbcbasics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/*
 * JdbcDemoApp.java
 * ------------------------------------------------------------
 * The main program. It walks through the whole topic step by step:
 *   1) connect and create the tables
 *   2) CRUD with StudentDao (Create, Read, Update, Delete)
 *   3) SQL injection: Statement vs PreparedStatement
 *   4) transactions: commit and rollback
 *   5) handling an SQLException (duplicate email)
 *
 * How to run (from the topic folder, where pom.xml is):
 *   mvn -q compile exec:java
 */
public class JdbcDemoApp {

    public static void main(String[] args) throws SQLException {

        // ---------- 1) Connect + schema ----------
        System.out.println("=== 1) Connecting to the database ===");
        try (Connection conn = Database.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Connected to " + meta.getDatabaseProductName()
                    + " using driver " + meta.getDriverName());
        }
        Database.createSchema();
        System.out.println("Tables created.");

        StudentDao dao = new StudentDao();

        // ---------- 2) CRUD ----------
        System.out.println("\n=== 2) CRUD with StudentDao ===");
        Student asha = dao.save(new Student("Asha", "asha@college.edu", 91));
        Student ben = dao.save(new Student("Ben", "ben@college.edu", 72));
        Student chen = dao.save(new Student("Chen", "chen@college.edu", 85));
        System.out.println("CREATE  -> saved " + asha + ", " + ben.name() + ", " + chen.name());

        System.out.println("READ ALL:");
        dao.findAll().forEach(s -> System.out.println("   " + s));

        Optional<Student> found = dao.findById(ben.id());
        System.out.println("READ ONE (id " + ben.id() + ") -> " + found.map(Student::name).orElse("not found"));
        System.out.println("READ ONE (id 99) -> " + dao.findById(99).map(Student::name).orElse("not found"));

        List<Student> toppers = dao.findByMinMarks(85);
        System.out.println("READ WHERE marks >= 85 -> " + toppers.stream().map(Student::name).toList());

        boolean updated = dao.updateMarks(ben.id(), 78);
        System.out.println("UPDATE  -> Ben's marks changed? " + updated + " -> " + dao.findById(ben.id()).orElseThrow());

        boolean deleted = dao.deleteById(chen.id());
        System.out.println("DELETE  -> Chen deleted? " + deleted + ". Students left: " + dao.count());

        // ---------- 3) SQL injection ----------
        System.out.println("\n=== 3) SQL injection ===");
        SqlInjectionDemo injection = new SqlInjectionDemo();
        String hackerInput = "' OR '1'='1";
        int unsafe = injection.countMatchesUnsafe(hackerInput);
        System.out.println("  Unsafe query returned " + unsafe + " student(s)  <-- leaked everyone!");
        int safe = injection.countMatchesSafe(hackerInput);
        System.out.println("  Safe query returned " + safe + " student(s)    <-- correct");

        // ---------- 4) Transactions ----------
        System.out.println("\n=== 4) Transactions ===");
        BankTransferService bank = new BankTransferService();
        System.out.println("Before:");
        bank.printBalances();

        System.out.println("Transfer 200 from Asha to Ben (no problems):");
        bank.transfer(1, 2, new BigDecimal("200.00"), false);
        bank.printBalances();

        System.out.println("Transfer 300 from Asha to Ben (crash in the middle):");
        bank.transfer(1, 2, new BigDecimal("300.00"), true);
        bank.printBalances();
        System.out.println("-> Balances unchanged: the half-done transfer was rolled back.");

        System.out.println("Transfer 5000 from Ben to Asha (not enough money -> CHECK constraint fails):");
        bank.transfer(2, 1, new BigDecimal("5000.00"), false);
        bank.printBalances();

        // ---------- 5) Handling SQLException ----------
        System.out.println("\n=== 5) Handling an SQLException ===");
        try {
            dao.save(new Student("Asha Again", "asha@college.edu", 60)); // email must be UNIQUE
        } catch (SQLException e) {
            System.out.println("Could not save: SQLState=" + e.getSQLState()
                    + ", errorCode=" + e.getErrorCode());
            System.out.println("Message: " + e.getMessage().lines().findFirst().orElse(""));
        }

        System.out.println("\nDone.");
    }
}
