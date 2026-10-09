package com.course.validation.controller;

import com.course.validation.dto.BookingRequest;
import com.course.validation.dto.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

/*
 * BookingController - date and number constraints (@Future, @Positive, @Max ...).
 *
 * To keep the focus on validation, this tiny controller calculates the price
 * itself. In a real project this logic would live in a BookingService (topic 33).
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@Valid @RequestBody BookingRequest request) {
        int discount = request.discountPercent() == null ? 0 : request.discountPercent();
        BigDecimal total = request.pricePerSeat()
                .multiply(BigDecimal.valueOf(request.seats()))
                .multiply(BigDecimal.valueOf(100 - discount))
                .divide(BigDecimal.valueOf(100));
        String code = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new BookingResponse(code, request.eventName(), request.eventDate(),
                request.seats(), total);
    }
}
