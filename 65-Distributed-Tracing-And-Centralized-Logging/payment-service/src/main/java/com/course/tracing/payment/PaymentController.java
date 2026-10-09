/*
 * PaymentController - POST /api/payments  body {"orderId":1001,"amount":2500.0}
 *
 * Shows two things:
 *   1. Normal log lines automatically get [payment-service,traceId,spanId,correlationId]
 *      because Micrometer Tracing puts those values into the logging MDC.
 *   2. A CUSTOM child span ("payment.fraud-check") created with the Observation API, so the
 *      slow part of the work is visible as its own bar in Zipkin.
 */
package com.course.tracing.payment;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    public record PaymentRequest(Long orderId, double amount) { }

    public record PaymentResponse(String paymentId, Long orderId, double amount, String status) { }

    // ObservationRegistry = Spring Boot's central "measuring" object. An Observation started on it
    // becomes BOTH a span (tracing) and a timer metric (/actuator/metrics) - two for one.
    private final ObservationRegistry observationRegistry;

    public PaymentController(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    @PostMapping
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        log.info("Payment request received for order {} amount {}", request.orderId(), request.amount());

        // Custom span: everything inside observe(...) is measured as "payment.fraud-check"
        boolean suspicious = Observation.createNotStarted("payment.fraud-check", observationRegistry)
                .lowCardinalityKeyValue("payment.method", "card")   // a tag shown in Zipkin
                .observe(() -> fraudCheck(request));

        String status = suspicious ? "DECLINED" : "APPROVED";
        PaymentResponse response = new PaymentResponse(
                "PAY-" + UUID.randomUUID().toString().substring(0, 8), request.orderId(), request.amount(), status);
        log.info("Payment {} for order {} -> {}", response.paymentId(), request.orderId(), status);
        return response;
    }

    /** Pretend fraud check: takes 100-300 ms; amounts above 50 000 are "suspicious". */
    private boolean fraudCheck(PaymentRequest request) {
        log.info("Running fraud check ...");   // this line shows the CHILD span's spanId
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(100, 300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return request.amount() > 50_000;
    }
}
