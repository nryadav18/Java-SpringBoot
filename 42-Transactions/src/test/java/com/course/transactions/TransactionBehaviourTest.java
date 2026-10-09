package com.course.transactions;

import com.course.transactions.dto.Dtos.DemoResult;
import com.course.transactions.dto.Dtos.TransferRequest;
import com.course.transactions.exception.InsufficientFundsException;
import com.course.transactions.service.AccountService;
import com.course.transactions.service.DemoService;
import com.course.transactions.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
 * TransactionBehaviourTest - every transaction rule from the lesson, proven by a test.
 * ----------------------------------------------------------------------------------
 * IMPORTANT: this test class is NOT @Transactional. If it were, every service call would JOIN
 * the test's transaction and the rollback/commit behaviour we want to observe would be hidden.
 * That's also why we use @SpringBootTest and not @DataJpaTest (which is transactional).
 *
 * Run:  mvn test
 */
@SpringBootTest
class TransactionBehaviourTest {

    @Autowired TransferService transferService;
    @Autowired AccountService accountService;
    @Autowired DemoService demoService;

    @BeforeEach
    void reset() {
        accountService.resetDemoData();          // Alice 1000.00, Bob 500.00
    }

    private BigDecimal balance(String account) {
        return accountService.balances().get(account);
    }

    @Test
    @DisplayName("A correct transfer moves the money and records it")
    void successfulTransfer() {
        transferService.transfer(new TransferRequest("ACC-1001", "ACC-1002", new BigDecimal("250.00")));

        assertThat(balance("ACC-1001")).isEqualByComparingTo("750.00");
        assertThat(balance("ACC-1002")).isEqualByComparingTo("750.00");
        assertThat(accountService.transfers()).hasSize(1);
    }

    @Test
    @DisplayName("Unchecked exception inside @Transactional -> full rollback; REQUIRES_NEW audit survives")
    void runtimeExceptionRollsBack() {
        DemoResult r = demoService.rollback();

        assertThat(r.moneyLost()).isFalse();
        assertThat(r.balancesAfter()).isEqualTo(r.balancesBefore());
        // the REQUIRED audit row was rolled back together with the transfer, REQUIRES_NEW was not:
        assertThat(r.auditLogsAfter()).hasSize(1).allMatch(line -> line.startsWith("REQUIRES_NEW"));
    }

    @Test
    @DisplayName("Without a transaction the half-finished transfer stays -> money lost")
    void noTransactionLosesMoney() {
        DemoResult r = demoService.noTransaction();

        assertThat(r.moneyLost()).isTrue();
        assertThat(r.balancesAfter().get("ACC-1001")).isEqualByComparingTo("900.00");
        assertThat(r.balancesAfter().get("ACC-1002")).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("Checked exception with default rules -> COMMIT (surprise!)")
    void checkedExceptionCommitsByDefault() {
        DemoResult r = demoService.checkedException();

        assertThat(r.error()).startsWith("FraudCheckException");
        assertThat(r.moneyLost()).isTrue();
    }

    @Test
    @DisplayName("Checked exception with rollbackFor -> rollback")
    void rollbackForMakesCheckedExceptionRollBack() {
        DemoResult r = demoService.checkedExceptionWithRollbackFor();

        assertThat(r.error()).startsWith("FraudCheckException");
        assertThat(r.moneyLost()).isFalse();
    }

    @Test
    @DisplayName("Self-invocation bypasses the proxy -> @Transactional ignored -> money lost")
    void selfInvocationIgnoresTransactional() {
        DemoResult r = demoService.selfInvocation();

        assertThat(r.moneyLost()).isTrue();
    }

    @Test
    @DisplayName("Insufficient funds -> exception, nothing changed, attempt audited")
    void insufficientFundsRollsBack() {
        assertThatThrownBy(() -> transferService.transfer(
                new TransferRequest("ACC-1001", "ACC-1002", new BigDecimal("5000.00"))))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(balance("ACC-1001")).isEqualByComparingTo("1000.00");
        assertThat(accountService.transfers()).isEmpty();
        assertThat(accountService.auditLogs()).hasSize(1);
    }

    @Test
    @DisplayName("@Version: the second concurrent update is rejected")
    void optimisticLockingRejectsStaleUpdate() {
        assertThatThrownBy(() -> accountService.simulateConcurrentEdit("ACC-1001"))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);

        assertThat(balance("ACC-1001")).isEqualByComparingTo("1010.00");   // only user A's +10 was saved
    }

    @Test
    @DisplayName("Total money in the bank never changes through correct transfers")
    void totalStaysTheSame() {
        BigDecimal before = accountService.totalMoneyInBank();
        transferService.transfer(new TransferRequest("ACC-1002", "ACC-1001", new BigDecimal("123.45")));

        assertThat(accountService.totalMoneyInBank()).isEqualByComparingTo(before);
    }
}
