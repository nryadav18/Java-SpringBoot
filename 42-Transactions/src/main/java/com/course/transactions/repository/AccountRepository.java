package com.course.transactions.repository;

import com.course.transactions.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Normal read - no lock.
    Optional<Account> findByAccountNumber(String accountNumber);

    // PESSIMISTIC LOCK: SELECT ... FOR UPDATE
    // The row stays locked until OUR transaction ends. Another transaction that wants to
    // lock the same row must WAIT. Must be called inside a transaction.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :number")
    Optional<Account> findForUpdate(@Param("number") String accountNumber);
}
