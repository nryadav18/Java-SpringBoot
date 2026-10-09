package com.course.transactions.controller;

import com.course.transactions.dto.Dtos.AccountResponse;
import com.course.transactions.dto.Dtos.AuditResponse;
import com.course.transactions.dto.Dtos.TransferRequest;
import com.course.transactions.dto.Dtos.TransferResponse;
import com.course.transactions.service.AccountService;
import com.course.transactions.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * BankController - the "real" API.
 *   GET  /api/accounts         balances + @Version numbers
 *   GET  /api/accounts/total   total money in the bank (should never change by transfers!)
 *   POST /api/transfers        transfer money (correct @Transactional implementation)
 *   GET  /api/transfers        successful transfers
 *   GET  /api/audit            every attempt (REQUIRES_NEW rows survive rollbacks)
 */
@RestController
@RequestMapping("/api")
public class BankController {

    private final AccountService accountService;
    private final TransferService transferService;

    public BankController(AccountService accountService, TransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;
    }

    @GetMapping("/accounts")
    public List<AccountResponse> accounts() {
        return accountService.allAccounts();
    }

    @GetMapping("/accounts/total")
    public Map<String, BigDecimal> total() {
        return Map.of("total", accountService.totalMoneyInBank());
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }

    @GetMapping("/transfers")
    public List<TransferResponse> transfers() {
        return accountService.transfers();
    }

    @GetMapping("/audit")
    public List<AuditResponse> audit() {
        return accountService.auditLogs();
    }
}
