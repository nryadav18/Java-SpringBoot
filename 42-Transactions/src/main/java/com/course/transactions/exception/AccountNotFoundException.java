package com.course.transactions.exception;

/* Unchecked -> 404 in the GlobalExceptionHandler. */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account " + accountNumber + " not found");
    }
}
