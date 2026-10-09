package com.course.transactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 42 - Transactions
 * -----------------------
 * A tiny bank with two accounts:  ACC-1001 Alice (1000.00)  and  ACC-1002 Bob (500.00).
 * Every /api/demo/... endpoint transfers 100.00 from Alice to Bob in a DIFFERENT way and
 * reports the balances before and after, so you can SEE when money is lost and when the
 * transaction protects you.
 *
 *   POST http://localhost:8080/api/demo/reset            back to 1000 / 500
 *   POST http://localhost:8080/api/demo/rollback         failure inside @Transactional  -> safe
 *   POST http://localhost:8080/api/demo/no-transaction   failure without a transaction  -> money lost!
 *   ... see requests.http for all of them.
 *
 * Watch the console: JpaTransactionManager DEBUG logs show every begin / commit / rollback.
 * Run:  mvn spring-boot:run
 */
@SpringBootApplication
public class TransactionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionsApplication.class, args);
    }
}
