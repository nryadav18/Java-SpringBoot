package com.course.validation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
 * BookingRequest - book seats for an event.
 * Shows number and date constraints: @Future, @Positive, @PositiveOrZero, @Max, @DecimalMin.
 *
 * Example JSON:
 * { "eventName":"Spring Conf", "eventDate":"2030-01-15", "seats":2,
 *   "discountPercent":10, "pricePerSeat":499.00 }
 */
public record BookingRequest(

        @NotBlank(message = "Event name is required")
        String eventName,

        @NotNull(message = "Event date is required")
        @Future(message = "Event date must be in the future")
        LocalDate eventDate,

        @NotNull(message = "Seats is required")
        @Positive(message = "Seats must be at least 1")
        @Max(value = 10, message = "You can book at most {value} seats at once")
        Integer seats,

        @PositiveOrZero(message = "Discount cannot be negative")
        @Max(value = 50, message = "Discount cannot be more than {value}%")
        Integer discountPercent,

        // For BigDecimal prices use @DecimalMin (works with decimals as text: "0.01")
        @NotNull(message = "Price per seat is required")
        @DecimalMin(value = "0.01", message = "Price per seat must be at least {value}")
        BigDecimal pricePerSeat
) {
}
