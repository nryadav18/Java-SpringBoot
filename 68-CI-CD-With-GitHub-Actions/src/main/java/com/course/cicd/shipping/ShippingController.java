package com.course.cicd.shipping;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * ShippingController.java
 *
 *   GET /api/shipping/quote?weightKg=2.5&express=true   -> 200 + ShippingQuote JSON
 *   GET /api/shipping/quote?weightKg=50                  -> 400 + ProblemDetail JSON
 */
@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/quote")
    public ShippingQuote quote(@RequestParam double weightKg,
                               @RequestParam(defaultValue = "false") boolean express) {
        return shippingService.quote(weightKg, express);
    }

    // Invalid weight -> 400 Bad Request with a ProblemDetail body (topic 35)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadInput(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
