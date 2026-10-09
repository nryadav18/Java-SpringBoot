package com.course.jpabasics.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/*
 * ProductRequest - what the CLIENT sends to us (POST / PUT body).
 * ----------------------------------------------------------------
 * A record = a short, immutable data carrier (topic 15). Perfect for DTOs.
 * Notice: NO id, NO createdAt/updatedAt. The client must not choose those - the server does.
 * This is exactly why we never accept the entity itself as @RequestBody.
 */
public record ProductRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @NotBlank @Size(max = 30) String sku,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price,
        @PositiveOrZero int stock
) {
}
