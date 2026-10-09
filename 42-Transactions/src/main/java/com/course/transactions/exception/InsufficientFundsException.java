package com.course.transactions.exception;

import java.math.BigDecimal;

/*
 * UNCHECKED (extends RuntimeException) -> by default @Transactional ROLLS BACK when it is thrown.
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountNumber, BigDecimal balance, BigDecimal requested) {
        super("Account " + accountNumber + " has only " + balance + ", cannot withdraw " + requested);
    }
}
