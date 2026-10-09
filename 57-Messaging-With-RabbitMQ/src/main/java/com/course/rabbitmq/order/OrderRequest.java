package com.course.rabbitmq.order;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/*
 * JSON body of POST /api/orders (validated as in Topic 34).
 * Tip: use an email ending with "@fail.test" to make the NotificationListener fail
 * and watch the retry + dead-letter queue behaviour.
 */
public record OrderRequest(
        @NotBlank String customerName,
        @NotBlank @Email String customerEmail,
        @NotBlank String product,
        @Positive int quantity,
        @Positive BigDecimal unitPrice) {
}
