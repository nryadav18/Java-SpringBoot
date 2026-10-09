package com.course.unittesting.bank;

import java.math.BigDecimal;

/*
 * Account.java
 * ------------------------------------------------------------
 * A bank account. It is a normal (mutable) class: the balance changes.
 * We use BigDecimal for money (never double - rounding errors!).
 */
public class Account {

    private final String id;
    private final String owner;
    private BigDecimal balance;

    public Account(String id, String owner, BigDecimal balance) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{id='" + id + "', owner='" + owner + "', balance=" + balance + "}";
    }
}
