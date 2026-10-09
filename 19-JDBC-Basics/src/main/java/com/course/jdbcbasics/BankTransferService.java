package com.course.jdbcbasics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * BankTransferService.java
 * ------------------------------------------------------------
 * What it shows:
 *   TRANSACTIONS: several SQL statements that must succeed or fail TOGETHER.
 *
 *   A money transfer = 2 updates:
 *      1) take money from account A
 *      2) add money to account B
 *   If step 2 fails after step 1 worked, money would simply disappear!
 *
 *   JDBC recipe:
 *      conn.setAutoCommit(false);   // start a transaction (by default every statement commits alone)
 *      ... do all updates ...
 *      conn.commit();               // make ALL changes permanent
 *      // on any error:
 *      conn.rollback();             // undo ALL changes since the start
 *
 *   In Spring you will get the same with ONE annotation: @Transactional (Topic 42).
 *
 * Not run directly - called from JdbcDemoApp.
 */
public class BankTransferService {

    /**
     * Transfers money between two accounts in ONE transaction.
     *
     * @param simulateCrash if true, we throw an error between the two updates
     *                      (like a power cut or a bug) to see the rollback in action.
     */
    public void transfer(int fromId, int toId, BigDecimal amount, boolean simulateCrash) throws SQLException {
        String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        String depositSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);              // BEGIN TRANSACTION
            try (PreparedStatement withdraw = conn.prepareStatement(withdrawSql);
                 PreparedStatement deposit = conn.prepareStatement(depositSql)) {

                // Step 1: take money out
                withdraw.setBigDecimal(1, amount);
                withdraw.setInt(2, fromId);
                withdraw.executeUpdate();
                System.out.println("  step 1 done: withdrew " + amount + " from account " + fromId);

                if (simulateCrash) {
                    throw new IllegalStateException("Server crashed between the two updates!");
                }

                // Step 2: put money in
                deposit.setBigDecimal(1, amount);
                deposit.setInt(2, toId);
                deposit.executeUpdate();
                System.out.println("  step 2 done: deposited " + amount + " to account " + toId);

                conn.commit();                      // COMMIT: both changes become permanent
                System.out.println("  COMMIT - transfer saved");

            } catch (SQLException | RuntimeException e) {
                conn.rollback();                    // ROLLBACK: undo step 1 as well
                // getMessage() of an SQLException can be several lines long; we show only the first line
                System.out.println("  ROLLBACK - " + e.getMessage().lines().findFirst().orElse(""));
            } finally {
                conn.setAutoCommit(true);           // good manners (important with connection pools)
            }
        }
    }

    /** Prints all accounts and their balances. */
    public void printBalances() throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, owner, balance FROM accounts ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.println("  Account " + rs.getInt("id") + " (" + rs.getString("owner") + "): "
                        + rs.getBigDecimal("balance"));
            }
        }
    }
}
