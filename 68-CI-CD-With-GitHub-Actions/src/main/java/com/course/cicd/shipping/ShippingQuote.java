package com.course.cicd.shipping;

import java.math.BigDecimal;

/*
 * ShippingQuote.java - the JSON answer of the API (a record used as a DTO).
 *
 *   { "weightKg": 2.5, "express": true, "cost": 15.00 }
 */
public record ShippingQuote(double weightKg, boolean express, BigDecimal cost) {
}
