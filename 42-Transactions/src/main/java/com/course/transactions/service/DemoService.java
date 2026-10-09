package com.course.transactions.service;

import com.course.transactions.dto.Dtos.DemoResult;
import com.course.transactions.dto.Dtos.TransferRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * DemoService - runs one scenario and reports what happened to the money.
 * ------------------------------------------------------------------------
 * This class is deliberately NOT @Transactional: it must stand OUTSIDE the scenarios,
 * like a bank auditor who looks at the balances before and after.
 * (If it were transactional, every scenario would join ITS transaction and the results
 *  would be completely different!)
 */
@Service
public class DemoService {

    static final String ALICE = "ACC-1001";
    static final String BOB = "ACC-1002";
    static final BigDecimal AMOUNT = new BigDecimal("100.00");

    /** A scenario is any code that may throw any exception (checked ones too). */
    @FunctionalInterface
    public interface Scenario {
        void run() throws Exception;
    }

    private final TransferService transferService;
    private final AccountService accountService;

    public DemoService(TransferService transferService, AccountService accountService) {
        this.transferService = transferService;
        this.accountService = accountService;
    }

    public DemoResult rollback() {
        return run("Failure midway INSIDE @Transactional",
                () -> transferService.transferThatFailsMidway(ALICE, BOB, AMOUNT),
                "The withdrawal was rolled back: no money lost. Only the REQUIRES_NEW audit row survived.");
    }

    public DemoResult noTransaction() {
        return run("Failure midway WITHOUT a transaction",
                () -> transferService.transferThatFailsMidwayWithoutTransaction(ALICE, BOB, AMOUNT),
                "Each save() committed on its own. Alice lost 100.00 and Bob never received it.");
    }

    public DemoResult checkedException() {
        return run("Checked exception with DEFAULT rollback rules",
                () -> transferService.transferWithCheckedException(ALICE, BOB, AMOUNT),
                "Checked exceptions do NOT trigger a rollback by default - the withdrawal was COMMITTED.");
    }

    public DemoResult checkedExceptionWithRollbackFor() {
        return run("Checked exception with rollbackFor = FraudCheckException.class",
                () -> transferService.transferWithCheckedExceptionRollbackFor(ALICE, BOB, AMOUNT),
                "rollbackFor turned the checked exception into a rollback - no money lost.");
    }

    public DemoResult selfInvocation() {
        return run("Self-invocation: this.transferThatFailsMidway(...)",
                () -> transferService.transferViaSelfInvocation(ALICE, BOB, AMOUNT),
                "The call did not go through the Spring proxy, so @Transactional was ignored - money lost.");
    }

    public DemoResult insufficientFunds() {
        return run("Correct transfer of 5000.00 (more than Alice has)",
                () -> transferService.transfer(new TransferRequest(ALICE, BOB, new BigDecimal("5000.00"))),
                "InsufficientFundsException (unchecked) -> rollback. The REQUIRES_NEW audit row of the attempt stays.");
    }

    public DemoResult optimisticLock() {
        return run("Two users update the same account at the same time (@Version)",
                () -> accountService.simulateConcurrentEdit(ALICE),
                "User A's +10 was saved. User B's +20 was REJECTED instead of silently overwriting A's change.");
    }

    // ---------------------------------------------------------------------
    private DemoResult run(String name, Scenario scenario, String lesson) {
        accountService.resetDemoData();
        Map<String, BigDecimal> before = accountService.balances();

        String error = null;
        try {
            scenario.run();
        } catch (Exception ex) {
            error = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        }

        Map<String, BigDecimal> after = accountService.balances();
        BigDecimal totalBefore = sum(before);
        BigDecimal totalAfter = sum(after);
        List<String> audit = accountService.auditLogs().stream()
                .map(a -> a.propagation() + " | " + a.message()).toList();

        return new DemoResult(name, before, after, totalBefore, totalAfter,
                totalAfter.compareTo(totalBefore) < 0, error, audit, lesson);
    }

    private static BigDecimal sum(Map<String, BigDecimal> balances) {
        return balances.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
