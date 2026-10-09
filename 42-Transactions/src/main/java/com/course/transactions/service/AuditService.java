package com.course.transactions.service;

import com.course.transactions.entity.AuditLog;
import com.course.transactions.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/*
 * AuditService - shows the two most important PROPAGATION types.
 * ---------------------------------------------------------------
 * Propagation = "what should happen if a transaction is ALREADY running when this method is called?"
 *
 *   REQUIRED (the default)  -> JOIN the existing transaction. If the caller rolls back,
 *                              this row is rolled back too. (No transaction yet? Start one.)
 *
 *   REQUIRES_NEW            -> PAUSE the caller's transaction, start a brand-new one,
 *                              COMMIT it when this method returns, then resume the caller.
 *                              This row survives even if the caller later rolls back.
 *
 * It is a SEPARATE bean from TransferService on purpose: @Transactional only works when the call
 * goes through the Spring proxy, i.e. from ANOTHER bean (see the self-invocation demo).
 */
@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logInNewTransaction(String message) {
        repository.save(new AuditLog(message, "REQUIRES_NEW"));
    }

    @Transactional(propagation = Propagation.REQUIRED)   // same as plain @Transactional
    public void logInCallerTransaction(String message) {
        repository.save(new AuditLog(message, "REQUIRED"));
    }
}
