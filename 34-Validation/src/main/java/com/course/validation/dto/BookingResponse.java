package com.course.validation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/* Confirmation sent back after a valid booking. */
public record BookingResponse(String bookingCode, String eventName, LocalDate eventDate,
                              int seats, BigDecimal totalPrice) {
}
