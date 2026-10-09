package com.course.kafka.order;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
 * Body of POST /api/orders.
 * Tips to see error handling:
 *   customerName "fail"       -> consumer throws -> retried 2 more times -> sent to orders.DLT
 *   a negative amount         -> consumer throws IllegalArgumentException -> NOT retried -> orders.DLT
 */
public record OrderRequest(@NotBlank String customerName, @NotNull BigDecimal amount) {
}
