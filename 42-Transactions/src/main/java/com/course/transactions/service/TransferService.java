package com.course.transactions.service;

import com.course.transactions.dto.Dtos.TransferRequest;
import com.course.transactions.dto.Dtos.TransferResponse;
import com.course.transactions.entity.Account;
import com.course.transactions.entity.Transfer;
import com.course.transactions.exception.AccountNotFoundException;
import com.course.transactions.exception.FraudCheckException;
import com.course.transactions.repository.AccountRepository;
import com.course.transactions.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/*
 * TransferService - moving money between accounts.
 * ================================================
 * A transfer is TWO updates (withdraw + deposit) that must succeed or fail TOGETHER.
 * That is exactly what a transaction guarantees (the "A" in ACID: Atomicity).
 *
 * NOTE: there is deliberately NO @Transactional on the class. Each method declares its own
 * behaviour, so the "bad" demo methods really run without (or with different) transactions.
 *
 * The demo methods all move 100.00 from Alice to Bob and then "crash" in different ways.
 */
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final AuditService auditService;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository,
                           AuditService auditService) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.auditService = auditService;
    }

    // =====================================================================
    // THE REAL, CORRECT TRANSFER
    // =====================================================================
    @Transactional    // REQUIRED, rollback on RuntimeException/Error, default isolation of the database
    public TransferResponse transfer(TransferRequest request) {
        // Audit the ATTEMPT in its own transaction -> kept even if this transfer fails.
        auditService.logInNewTransaction("Transfer requested: " + request.amount() + " from "
                + request.fromAccount() + " to " + request.toAccount());

        if (request.fromAccount().equals(request.toAccount())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // PESSIMISTIC locks (SELECT ... FOR UPDATE). Always lock in the SAME order (alphabetical)
        // so two opposite transfers (A->B and B->A) can never wait for each other forever (deadlock).
        boolean fromFirst = request.fromAccount().compareTo(request.toAccount()) < 0;
        Account first = lock(fromFirst ? request.fromAccount() : request.toAccount());
        Account second = lock(fromFirst ? request.toAccount() : request.fromAccount());
        Account from = fromFirst ? first : second;
        Account to = fromFirst ? second : first;

        from.withdraw(request.amount());   // may throw InsufficientFundsException -> ROLLBACK
        to.deposit(request.amount());
        // No save() needed for the accounts: they are MANAGED -> dirty checking at commit (topic 38).

        Transfer saved = transferRepository.save(
                new Transfer(request.fromAccount(), request.toAccount(), request.amount()));
        return TransferResponse.from(saved);
        // Method returns normally -> the proxy COMMITS: both balances + transfer row, all or nothing.
    }

    // =====================================================================
    // DEMO 1: failure in the middle, WITH @Transactional  -> everything rolled back
    // =====================================================================
    @Transactional
    public void transferThatFailsMidway(String from, String to, BigDecimal amount) {
        auditService.logInNewTransaction("[REQUIRES_NEW] attempt " + amount + " " + from + " -> " + to);
        auditService.logInCallerTransaction("[REQUIRED] attempt " + amount + " " + from + " -> " + to);

        withdrawAndSave(from, amount);                 // step 1 done ...
        simulateCrash();                               // ... BOOM before step 2
        depositAndSave(to, amount);                    // never reached
    }

    // =====================================================================
    // DEMO 2: the same code WITHOUT any transaction  -> money disappears!
    // =====================================================================
    public void transferThatFailsMidwayWithoutTransaction(String from, String to, BigDecimal amount) {
        withdrawAndSave(from, amount);                 // save() commits immediately in its own tiny transaction
        simulateCrash();
        depositAndSave(to, amount);
    }

    // =====================================================================
    // DEMO 3: CHECKED exception with default rules -> NO rollback, partial work is committed!
    // =====================================================================
    @Transactional
    public void transferWithCheckedException(String from, String to, BigDecimal amount) throws FraudCheckException {
        withdrawAndSave(from, amount);
        throw new FraudCheckException("Fraud check service says: suspicious transfer");
    }

    // =====================================================================
    // DEMO 4: CHECKED exception + rollbackFor  -> rolled back correctly
    // =====================================================================
    @Transactional(rollbackFor = FraudCheckException.class)
    public void transferWithCheckedExceptionRollbackFor(String from, String to, BigDecimal amount)
            throws FraudCheckException {
        withdrawAndSave(from, amount);
        throw new FraudCheckException("Fraud check service says: suspicious transfer");
    }

    // =====================================================================
    // DEMO 5: SELF-INVOCATION - calling a @Transactional method on "this"
    // =====================================================================
    public void transferViaSelfInvocation(String from, String to, BigDecimal amount) {
        // "this" is the REAL object, not the Spring proxy. The proxy is the one that starts
        // transactions (topic 27: AOP). So @Transactional on transferThatFailsMidway is IGNORED here
        // and the method runs WITHOUT a transaction -> same money loss as demo 2.
        this.transferThatFailsMidway(from, to, amount);
    }

    // =====================================================================
    // helpers
    // =====================================================================
    private Account lock(String accountNumber) {
        return accountRepository.findForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private Account load(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    // save() is called explicitly so that the "no transaction" demos really write to the database.
    // (Inside a transaction it would not be necessary - dirty checking does it.)
    private void withdrawAndSave(String accountNumber, BigDecimal amount) {
        Account account = load(accountNumber);
        account.withdraw(amount);
        accountRepository.save(account);
    }

    private void depositAndSave(String accountNumber, BigDecimal amount) {
        Account account = load(accountNumber);
        account.deposit(amount);
        accountRepository.save(account);
    }

    private void simulateCrash() {
        throw new IllegalStateException("Simulated crash: the network to the other bank went down!");
    }
}
