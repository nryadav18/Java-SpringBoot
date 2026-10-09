/*
 * OrderController - POST /api/orders  body {"item":"Pizza Margherita","amount":450.0}
 * ---------------------------------------------------------------
 * The span tree for ONE request looks like this (see it in Zipkin):
 *
 *   order-service   http post /api/orders            (SERVER span - created automatically)
 *   |-- order-service   order.validate               (CUSTOM span - Observation API below)
 *   `-- order-service   http post                    (CLIENT span - RestClient, automatic)
 *       `-- payment-service  http post /api/payments (SERVER span - automatic, same traceId!)
 *           `-- payment-service  payment.fraud-check (CUSTOM span in payment-service)
 *
 * GET /api/orders/trace-info shows the current traceId/spanId from Java code (Tracer API).
 */
package com.course.tracing.order;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    public record OrderRequest(String item, double amount) { }

    public record PaymentRequest(Long orderId, double amount) { }

    public record PaymentResponse(String paymentId, Long orderId, double amount, String status) { }

    public record OrderResponse(Long orderId, String item, double amount,
                                String paymentStatus, String paymentId, String traceId) { }

    private final RestClient paymentRestClient;
    private final ObservationRegistry observationRegistry;
    private final Tracer tracer;
    private final AtomicLong ids = new AtomicLong(1000);

    public OrderController(RestClient paymentRestClient, ObservationRegistry observationRegistry, Tracer tracer) {
        this.paymentRestClient = paymentRestClient;
        this.observationRegistry = observationRegistry;
        this.tracer = tracer;
    }

    @PostMapping
    public OrderResponse placeOrder(@RequestBody OrderRequest request) {
        log.info("New order received: {} for {}", request.item(), request.amount());

        // 1) A custom span around our own validation logic
        Observation.createNotStarted("order.validate", observationRegistry)
                .observe(() -> validate(request));

        long orderId = ids.incrementAndGet();

        // 2) Remote call - the traceparent + correlation-id headers are added automatically
        log.info("Calling payment-service for order {}", orderId);
        PaymentResponse payment = paymentRestClient.post()
                .uri("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new PaymentRequest(orderId, request.amount()))
                .retrieve()
                .body(PaymentResponse.class);

        log.info("Order {} finished, payment status {}", orderId, payment.status());
        return new OrderResponse(orderId, request.item(), request.amount(),
                payment.status(), payment.paymentId(), currentTraceId());
    }

    @GetMapping("/trace-info")
    public Map<String, String> traceInfo() {
        Span span = tracer.currentSpan();
        String correlation = tracer.getBaggage(CorrelationIdFilter.CORRELATION_ID) != null
                ? tracer.getBaggage(CorrelationIdFilter.CORRELATION_ID).get() : null;
        log.info("trace-info requested");
        return Map.of(
                "traceId", span != null ? span.context().traceId() : "none",
                "spanId", span != null ? span.context().spanId() : "none",
                "correlationId", correlation != null ? correlation : "none");
    }

    private void validate(OrderRequest request) {
        log.info("Validating order ...");
        if (request.item() == null || request.item().isBlank() || request.amount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "item and a positive amount are required");
        }
    }

    private String currentTraceId() {
        Span span = tracer.currentSpan();
        return span != null ? span.context().traceId() : "none";
    }
}
