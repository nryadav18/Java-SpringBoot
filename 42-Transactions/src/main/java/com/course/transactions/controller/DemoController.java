package com.course.transactions.controller;

import com.course.transactions.dto.Dtos.DemoResult;
import com.course.transactions.service.AccountService;
import com.course.transactions.service.DemoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
 * DemoController - one endpoint per transaction scenario.
 * Each one first RESETS the balances (Alice 1000, Bob 500), runs the scenario (transfer 100),
 * and returns before/after balances, the error and the audit log.
 * Look at "moneyLost" in the response - and at the transaction logs in the console.
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final DemoService demo;
    private final AccountService accountService;

    public DemoController(DemoService demo, AccountService accountService) {
        this.demo = demo;
        this.accountService = accountService;
    }

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        accountService.resetDemoData();
        return Map.of("status", "reset", "balances", accountService.balances());
    }

    @PostMapping("/rollback")
    public DemoResult rollback() {
        return demo.rollback();
    }

    @PostMapping("/no-transaction")
    public DemoResult noTransaction() {
        return demo.noTransaction();
    }

    @PostMapping("/checked-exception")
    public DemoResult checkedException() {
        return demo.checkedException();
    }

    @PostMapping("/checked-exception-rollback-for")
    public DemoResult checkedExceptionRollbackFor() {
        return demo.checkedExceptionWithRollbackFor();
    }

    @PostMapping("/self-invocation")
    public DemoResult selfInvocation() {
        return demo.selfInvocation();
    }

    @PostMapping("/insufficient-funds")
    public DemoResult insufficientFunds() {
        return demo.insufficientFunds();
    }

    @PostMapping("/optimistic-lock")
    public DemoResult optimisticLock() {
        return demo.optimisticLock();
    }
}
