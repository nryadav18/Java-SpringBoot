package com.course.transactions.config;

import com.course.transactions.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/*
 * Creates the two demo accounts at startup: ACC-1001 Alice 1000.00, ACC-1002 Bob 500.00.
 * (resetDemoData creates them if they don't exist yet.)
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final AccountService accountService;

    public DataSeeder(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) {
        accountService.resetDemoData();
    }
}
