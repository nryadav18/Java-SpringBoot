package com.course.unittesting.bank;

/*
 * NotificationService.java
 * ------------------------------------------------------------
 * Sends SMS/e-mail alerts. A second dependency of BankAccountService.
 * In tests we mock it and VERIFY that it was (or was not) called -
 * we certainly don't want tests to send real SMS messages!
 */
public interface NotificationService {

    void sendLowBalanceAlert(String accountId, String message);
}
