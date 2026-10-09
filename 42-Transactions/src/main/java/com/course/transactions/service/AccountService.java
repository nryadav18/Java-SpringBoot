package com.course.transactions.service;

import com.course.transactions.dto.Dtos.AccountResponse;
import com.course.transactions.dto.Dtos.AuditResponse;
import com.course.transactions.dto.Dtos.TransferResponse;
import com.course.transactions.entity.Account;
import com.course.transactions.exception.AccountNotFoundException;
import com.course.transactions.repository.AccountRepository;
import com.course.transactions.repository.AuditLogRepository;
import com.course.transactions.repository.TransferRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * AccountService - reads, the demo reset, and the OPTIMISTIC LOCKING demo.
 */
@Service
public class AccountService {

    public static final Map<String, BigDecimal> INITIAL_BALANCES = Map.of(
            "ACC-1001", new BigDecimal("1000.00"),
            "ACC-1002", new BigDecimal("500.00"));

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final AuditLogRepository auditLogRepository;

    public AccountService(AccountRepository accountRepository, TransferRepository transferRepository,
                          AuditLogRepository auditLogRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // readOnly = true: a hint that nothing is written. Hibernate skips dirty checking (faster),
    // and some databases/drivers optimise read-only transactions.
    @Transactional(readOnly = true)
    public List<AccountResponse> allAccounts() {
        return accountRepository.findAll(Sort.by("accountNumber")).stream().map(AccountResponse::from).toList();
    }

    // A report that must see ONE consistent picture of all balances. REPEATABLE_READ asks the
    // database: rows I have read must not change under me until I finish. (H2/MySQL/PostgreSQL support it.)
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public BigDecimal totalMoneyInBank() {
        return accountRepository.findAll().stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> balances() {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        accountRepository.findAll(Sort.by("accountNumber")).forEach(a -> result.put(a.getAccountNumber(), a.getBalance()));
        return result;
    }

    @Transactional(readOnly = true)
    public List<TransferResponse> transfers() {
        return transferRepository.findAll().stream().map(TransferResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditResponse> auditLogs() {
        return auditLogRepository.findAll().stream().map(AuditResponse::from).toList();
    }

    /** Puts the demo data back: balances 1000 / 500, no transfers, no audit logs. */
    @Transactional
    public void resetDemoData() {
        transferRepository.deleteAllInBatch();
        auditLogRepository.deleteAllInBatch();
        INITIAL_BALANCES.forEach((number, balance) -> {
            Account account = accountRepository.findByAccountNumber(number)
                    .orElseGet(() -> accountRepository.save(new Account(number, ownerOf(number), balance)));
            account.setBalance(balance);
        });
    }

    private static String ownerOf(String number) {
        return number.equals("ACC-1001") ? "Alice" : "Bob";
    }

    /**
     * OPTIMISTIC LOCKING demo - two "users" edit the same account at the same time.
     * NO @Transactional here: each repository call runs in its own short transaction, so
     * copyA and copyB are two independent, DETACHED snapshots - like two browser tabs.
     */
    public void simulateConcurrentEdit(String accountNumber) {
        Account copyA = accountRepository.findByAccountNumber(accountNumber)        // user A reads (version = n)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        Account copyB = accountRepository.findByAccountNumber(accountNumber)        // user B reads (version = n)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        copyA.deposit(new BigDecimal("10.00"));
        accountRepository.save(copyA);     // UPDATE ... SET version = n+1 WHERE id = ? AND version = n  -> OK

        copyB.deposit(new BigDecimal("20.00"));
        accountRepository.save(copyB);     // still says version = n -> conflict
                                           // -> ObjectOptimisticLockingFailureException (B's change is NOT lost silently)
    }
}
