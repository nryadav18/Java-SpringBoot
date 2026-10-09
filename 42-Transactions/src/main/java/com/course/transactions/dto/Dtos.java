package com.course.transactions.dto;

import com.course.transactions.entity.Account;
import com.course.transactions.entity.AuditLog;
import com.course.transactions.entity.Transfer;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/* All request/response records of this topic, grouped in one file. */
public final class Dtos {

    private Dtos() {
    }

    public record TransferRequest(@NotBlank String fromAccount,
                                  @NotBlank String toAccount,
                                  @NotNull @DecimalMin("0.01") BigDecimal amount) {
    }

    public record AccountResponse(String accountNumber, String ownerName, BigDecimal balance, Long version) {
        public static AccountResponse from(Account a) {
            return new AccountResponse(a.getAccountNumber(), a.getOwnerName(), a.getBalance(), a.getVersion());
        }
    }

    public record TransferResponse(Long id, String fromAccount, String toAccount, BigDecimal amount,
                                   LocalDateTime createdAt) {
        public static TransferResponse from(Transfer t) {
            return new TransferResponse(t.getId(), t.getFromAccount(), t.getToAccount(), t.getAmount(),
                    t.getCreatedAt());
        }
    }

    public record AuditResponse(Long id, String message, String propagation, LocalDateTime createdAt) {
        public static AuditResponse from(AuditLog a) {
            return new AuditResponse(a.getId(), a.getMessage(), a.getPropagation(), a.getCreatedAt());
        }
    }

    /** What each /api/demo/... endpoint returns: balances before/after + what went wrong. */
    public record DemoResult(String scenario,
                             Map<String, BigDecimal> balancesBefore,
                             Map<String, BigDecimal> balancesAfter,
                             BigDecimal totalBefore,
                             BigDecimal totalAfter,
                             boolean moneyLost,
                             String error,
                             List<String> auditLogsAfter,
                             String lesson) {
    }
}
