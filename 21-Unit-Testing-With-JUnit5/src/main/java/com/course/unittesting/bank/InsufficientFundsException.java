package com.course.unittesting.bank;

import java.math.BigDecimal;

/** Thrown when a withdrawal/transfer is bigger than the available balance. */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountId, BigDecimal balance, BigDecimal requested) {
        super("Insufficient funds in " + accountId + ": balance " + balance + ", requested " + requested);
    }
}
