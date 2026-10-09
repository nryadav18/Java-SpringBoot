package com.course.unittesting.bank;

import java.util.Optional;

/*
 * AccountRepository.java
 * ------------------------------------------------------------
 * The "data access" dependency of BankAccountService.
 * It is an INTERFACE: the real implementation could use JDBC (Topic 19) or JPA (Topic 38).
 *
 * In unit tests we do NOT want a real database (slow, needs setup).
 * Because the service depends on this interface, we can give it a MOCK instead.
 * (Spring Data repositories look almost exactly like this - Topic 38.)
 */
public interface AccountRepository {

    Optional<Account> findById(String id);

    Account save(Account account);
}
