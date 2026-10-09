package com.course.webflux.stream;

import java.math.BigDecimal;
import java.time.LocalTime;

/* One "price update" event sent to the browser through Server-Sent Events. */
public record PriceTick(long sequence, String product, BigDecimal price, LocalTime time) {
}
