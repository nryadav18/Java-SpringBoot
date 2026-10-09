package com.course.transactions.exception;

/*
 * A CHECKED exception (extends Exception, not RuntimeException).
 *
 * IMPORTANT: by default, @Transactional does NOT roll back for checked exceptions!
 * The transaction COMMITS whatever was done before the exception was thrown.
 * You must ask for a rollback explicitly:  @Transactional(rollbackFor = FraudCheckException.class)
 */
public class FraudCheckException extends Exception {

    public FraudCheckException(String message) {
        super(message);
    }
}
