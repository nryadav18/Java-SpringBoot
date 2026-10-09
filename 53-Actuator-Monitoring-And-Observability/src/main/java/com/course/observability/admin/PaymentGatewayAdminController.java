package com.course.observability.admin;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.course.observability.health.PaymentGateway;

/*
 * DEMO ONLY: switch the fake payment gateway up/down to watch /actuator/health change.
 *   POST /api/admin/payment-gateway?up=false   -> health becomes DOWN (HTTP 503)
 *   POST /api/admin/payment-gateway?up=true    -> back to UP
 */
@RestController
@RequestMapping("/api/admin/payment-gateway")
public class PaymentGatewayAdminController {

    private final PaymentGateway paymentGateway;

    public PaymentGatewayAdminController(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @PostMapping
    public Map<String, Boolean> setStatus(@RequestParam boolean up) {
        paymentGateway.setUp(up);
        return Map.of("paymentGatewayUp", paymentGateway.isUp());
    }
}
