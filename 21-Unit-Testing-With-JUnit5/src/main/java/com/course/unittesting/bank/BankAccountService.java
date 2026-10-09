package com.course.unittesting.bank;

import java.math.BigDecimal;

/*
 * BankAccountService.java
 * ------------------------------------------------------------
 * Business logic for bank accounts. This is the class we unit-test with Mockito
 * (see BankAccountServiceTest).
 *
 * Notice the design that makes it TESTABLE:
 *   - dependencies are INTERFACES (AccountRepository, NotificationService)
 *   - they are passed in through the CONSTRUCTOR (dependency injection, no 'new' inside)
 * So a test can pass in mocks. This is exactly how Spring services are written (Topics 22-24).
 */
public class BankAccountService {

    /** If the balance drops below this after a withdrawal, the customer gets an alert. */
    static final BigDecimal LOW_BALANCE_LIMIT = new BigDecimal("100");

    private final AccountRepository repository;
    private final NotificationService notificationService;

    public BankAccountService(AccountRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public BigDecimal getBalance(String accountId) {
        return findAccount(accountId).getBalance();
    }

    public Account deposit(String accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = findAccount(accountId);
        account.setBalance(account.getBalance().add(amount));
        return repository.save(account);
    }

    public Account withdraw(String accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = findAccount(accountId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId, account.getBalance(), amount);
        }

        account.setBalance(account.getBalance().subtract(amount));
        Account saved = repository.save(account);

        if (saved.getBalance().compareTo(LOW_BALANCE_LIMIT) < 0) {
            notificationService.sendLowBalanceAlert(accountId,
                    "Your balance is low: " + saved.getBalance());
        }
        return saved;
    }

    public void transfer(String fromId, String toId, BigDecimal amount) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        withdraw(fromId, amount);
        deposit(toId, amount);
    }

    // ---------- helpers ----------
    private Account findAccount(String accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
