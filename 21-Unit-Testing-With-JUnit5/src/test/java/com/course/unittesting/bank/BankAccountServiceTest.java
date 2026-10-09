package com.course.unittesting.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/*
 * BankAccountServiceTest.java
 * ------------------------------------------------------------
 * Unit-testing a service that has DEPENDENCIES, using Mockito.
 *
 *   @ExtendWith(MockitoExtension.class) : let Mockito process the annotations below
 *   @Mock         : create a fake AccountRepository / NotificationService (no database, no SMS!)
 *   @InjectMocks  : create a REAL BankAccountService and pass the mocks into its constructor
 *
 *   when(mock.method(...)).thenReturn(value)  : "stubbing" - program the fake's answer
 *   verify(mock).method(...)                  : check that the service CALLED the dependency
 *   verify(mock, never()).method(...)         : check that it did NOT call it
 *   ArgumentCaptor                            : capture the argument that was passed, to inspect it
 *
 * Run:  mvn test -Dtest=BankAccountServiceTest
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BankAccountService")
class BankAccountServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BankAccountService service;    // = new BankAccountService(repository, notificationService)

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    // Small helper to build test data ("test fixture")
    private static Account account(String id, String balance) {
        return new Account(id, "Owner of " + id, new BigDecimal(balance));
    }

    // ================================================================
    @Nested
    @DisplayName("getBalance")
    class GetBalance {

        @Test
        @DisplayName("returns the balance of an existing account")
        void returnsBalance() {
            // Arrange: program the mock
            when(repository.findById("ACC-1")).thenReturn(Optional.of(account("ACC-1", "500")));

            // Act
            BigDecimal balance = service.getBalance("ACC-1");

            // Assert
            assertEquals(new BigDecimal("500"), balance);
        }

        @Test
        @DisplayName("throws AccountNotFoundException for an unknown id")
        void unknownAccount() {
            when(repository.findById("NOPE")).thenReturn(Optional.empty());

            AccountNotFoundException ex =
                    assertThrows(AccountNotFoundException.class, () -> service.getBalance("NOPE"));

            assertEquals("Account not found: NOPE", ex.getMessage());
        }
    }

    // ================================================================
    @Nested
    @DisplayName("deposit")
    class Deposit {

        @Test
        @DisplayName("adds the amount and saves the account")
        void depositAddsAndSaves() {
            when(repository.findById("ACC-1")).thenReturn(Optional.of(account("ACC-1", "100")));
            // save(...) should return whatever it receives (like a real repository would)
            when(repository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Account result = service.deposit("ACC-1", new BigDecimal("50"));

            assertThat(result.getBalance()).isEqualByComparingTo("150");
            verify(repository).save(accountCaptor.capture());          // save was called once...
            assertThat(accountCaptor.getValue().getId()).isEqualTo("ACC-1"); // ...with this account
        }

        @Test
        @DisplayName("rejects zero or negative amounts without touching the repository")
        void rejectsNonPositiveAmounts() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.deposit("ACC-1", BigDecimal.ZERO));
            assertThrows(IllegalArgumentException.class,
                    () -> service.deposit("ACC-1", new BigDecimal("-10")));

            verifyNoInteractions(repository);   // validation happens BEFORE any DB access
        }
    }

    // ================================================================
    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        @DisplayName("reduces the balance and sends no alert when the balance stays high")
        void withdrawWithoutAlert() {
            when(repository.findById("ACC-1")).thenReturn(Optional.of(account("ACC-1", "1000")));
            when(repository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

            Account result = service.withdraw("ACC-1", new BigDecimal("300"));

            assertThat(result.getBalance()).isEqualByComparingTo("700");
            verify(notificationService, never()).sendLowBalanceAlert(anyString(), anyString());
        }

        @Test
        @DisplayName("sends a low-balance alert when the balance drops below 100")
        void withdrawWithAlert() {
            when(repository.findById("ACC-1")).thenReturn(Optional.of(account("ACC-1", "150")));
            when(repository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

            service.withdraw("ACC-1", new BigDecimal("80"));   // 150 - 80 = 70 -> low

            verify(notificationService, times(1))
                    .sendLowBalanceAlert(eq("ACC-1"), eq("Your balance is low: 70"));
        }

        @Test
        @DisplayName("throws InsufficientFundsException and saves nothing when money is missing")
        void insufficientFunds() {
            when(repository.findById("ACC-1")).thenReturn(Optional.of(account("ACC-1", "50")));

            // AssertJ style for exceptions
            assertThatThrownBy(() -> service.withdraw("ACC-1", new BigDecimal("80")))
                    .isInstanceOf(InsufficientFundsException.class)
                    .hasMessageContaining("balance 50")
                    .hasMessageContaining("requested 80");

            verify(repository, never()).save(any());
            verifyNoInteractions(notificationService);
        }
    }

    // ================================================================
    @Nested
    @DisplayName("transfer")
    class Transfer {

        @Test
        @DisplayName("moves money from one account to another")
        void transfersMoney() {
            Account from = account("A", "500");
            Account to = account("B", "100");
            when(repository.findById("A")).thenReturn(Optional.of(from));
            when(repository.findById("B")).thenReturn(Optional.of(to));
            when(repository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

            service.transfer("A", "B", new BigDecimal("200"));

            assertThat(from.getBalance()).isEqualByComparingTo("300");
            assertThat(to.getBalance()).isEqualByComparingTo("300");
            verify(repository, times(2)).save(any(Account.class));     // both accounts saved
        }

        @Test
        @DisplayName("refuses a transfer to the same account")
        void sameAccount() {
            assertThatThrownBy(() -> service.transfer("A", "A", BigDecimal.TEN))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Cannot transfer to the same account");

            verifyNoInteractions(repository, notificationService);
        }
    }
}
