package com.course.shopeasy.product;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Body of POST /api/v1/products and PUT /api/v1/products/{id} (ADMIN only).
 * Every rule here is checked BEFORE the service runs, thanks to @Valid in the controller (topic 34).
 */
public record ProductRequest(
        @Schema(example = "SPORT-001", description = "Unique stock keeping unit: capital letters, digits and dashes")
        @NotBlank
        @Pattern(regexp = "^[A-Z0-9-]{3,40}$", message = "must be 3-40 characters: A-Z, 0-9 or '-'")
        String sku,

        @Schema(example = "Yoga Mat")
        @NotBlank @Size(max = 150)
        String name,

        @Schema(example = "6 mm thick, non-slip")
        @Size(max = 1000)
        String description,

        @Schema(example = "1499.00")
        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 10, fraction = 2)      // fits DECIMAL(12,2) in the database
        BigDecimal price,

        @Schema(example = "25")
        @NotNull @PositiveOrZero
        Integer stockQuantity,

        @Schema(example = "https://images.example.com/yoga-mat.png")
        @Size(max = 500)
        String imageUrl,

        @Schema(example = "1")
        @NotNull
        Long categoryId) {
}
