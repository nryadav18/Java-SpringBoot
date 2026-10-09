# 42 - Transactions

ACID with a bank transfer, `@Transactional` (class vs method), the AOP proxy behind it, rollback rules
(unchecked by default, `rollbackFor`), propagation (`REQUIRED`, `REQUIRES_NEW` demo), isolation levels,
`readOnly`, the self-invocation pitfall, optimistic locking with `@Version` and pessimistic locking.
Domain: a bank with Alice (ACC-1001, 1000.00) and Bob (ACC-1002, 500.00).
Open **lesson.html** in a browser for the full lesson.

## Files
- `pom.xml` - Spring Boot 3.5.6 with web, data-jpa, validation, H2.
- `application.properties` - `JpaTransactionManager` DEBUG logging so you SEE begin/commit/rollback.
- `entity/Account.java` - balance + `@Version`; `withdraw()` throws `InsufficientFundsException`.
- `entity/Transfer.java`, `entity/AuditLog.java` - transfer history and audit trail.
- `repository/AccountRepository.java` - `@Lock(PESSIMISTIC_WRITE)` query (SELECT ... FOR UPDATE).
- `service/TransferService.java` - the correct transfer + 5 "failure" variants (rollback, no transaction, checked exception, rollbackFor, self-invocation).
- `service/AuditService.java` - `REQUIRED` vs `REQUIRES_NEW`.
- `service/AccountService.java` - `readOnly`, isolation, demo reset, optimistic-lock simulation.
- `service/DemoService.java` - runs a scenario and reports balances before/after.
- `controller/BankController.java` - real API; `controller/DemoController.java` - `/api/demo/*` scenarios.
- `exception/*` - unchecked/checked exceptions, 404/409/422 handlers.
- `src/test/.../TransactionBehaviourTest.java` - 9 tests proving every rule.
- `requests.http` - all requests.

## Run
```
cd "D:\Java-SpringBoot\42-Transactions"
mvn spring-boot:run
```
Then `POST http://localhost:8080/api/demo/rollback` and `POST .../api/demo/no-transaction` and compare `moneyLost`.
Tests: `mvn test`
