package com.course.shopeasy.product;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Optional search filters, bound from QUERY parameters:  ?q=book&categoryId=2&minPrice=100&maxPrice=900&inStock=true
 *
 * Spring MVC creates this record from the matching query parameters (a "model attribute").
 * Every field may be null = "no filter".
 */
public record ProductSearchCriteria(
        @Parameter(description = "Text to search in name and description", example = "phone")
        @Size(max = 100)
        String q,

        @Parameter(description = "Only products of this category", example = "1")
        Long categoryId,

        @Parameter(description = "Minimum price (inclusive)", example = "100")
        @PositiveOrZero
        BigDecimal minPrice,

        @Parameter(description = "Maximum price (inclusive)", example = "30000")
        @PositiveOrZero
        BigDecimal maxPrice,

        @Parameter(description = "true = only products with stock > 0")
        Boolean inStock) {

    /** Cross-field rule: a class-level check that minPrice is not greater than maxPrice. */
    @AssertTrue(message = "minPrice must not be greater than maxPrice")
    @io.swagger.v3.oas.annotations.Hidden
    public boolean isPriceRangeValid() {
        return minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) <= 0;
    }
}
