package com.course.transactions.entity;

import com.course.transactions.exception.InsufficientFundsException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

/*
 * Account - a bank account.
 * -------------------------
 * @Version enables OPTIMISTIC LOCKING:
 *   - Hibernate adds 1 to "version" on every UPDATE,
 *   - and the UPDATE says  "... WHERE id = ? AND version = ?"  (the version we READ).
 *   - If someone else updated the row in the meantime, the version no longer matches,
 *     0 rows are updated and Hibernate throws an OptimisticLockException
 *     (Spring: ObjectOptimisticLockingFailureException). No change is silently overwritten.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false, length = 100)
    private String ownerName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Version
    private Long version;      // managed by Hibernate - never set it yourself

    protected Account() {
    }

    public Account(String accountNumber, String ownerName, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    /** Business rule lives in the entity: you cannot withdraw more than you have. */
    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountNumber, balance, amount);
        }
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }   // used only by the demo reset
    public Long getVersion() { return version; }
}
