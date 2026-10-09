package com.course.requesthandling;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * The JSON body for PATCH (partial update). Every field is OPTIONAL.
 * We use wrapper types (Integer, not int) so a missing field arrives as null:
 *
 *   {"price": 699}          -> only the price changes
 *   {"stock": 0}            -> only the stock changes (0 is a real value, null means "not sent")
 */
public record ProductPatchRequest(
        String name,
        String category,
        BigDecimal price,
        @JsonProperty("stock") Integer quantityInStock) {
}
