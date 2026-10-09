package com.course.unittesting.bank;

/** Thrown when no account exists for the given id. Unchecked (extends RuntimeException). */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountId) {
        super("Account not found: " + accountId);
    }
}
