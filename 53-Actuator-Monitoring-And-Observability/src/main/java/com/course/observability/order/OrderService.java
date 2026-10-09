package com.course.observability.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.course.observability.health.PaymentGateway;
import com.course.observability.order.Order.OrderStatus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/*
 * Business logic + CUSTOM METRICS with Micrometer.
 *
 * Micrometer is to metrics what SLF4J is to logging: one simple API in your code,
 * many possible back-ends (Prometheus, Datadog, CloudWatch, New Relic ...).
 *
 * MeterRegistry = the "notebook" where all meters (metrics) are registered. Spring Boot
 * creates it for us; we just inject it.
 *
 * Meter types used here:
 *   Counter             - a number that only goes UP          (orders placed, orders failed)
 *   Timer               - how long something takes + how often (order processing time)
 *   Gauge               - a current value that goes up AND down (pending orders right now)
 *   DistributionSummary - distribution of amounts that are not time (order value in EUR)
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    private final MeterRegistry registry;
    private final PaymentGateway paymentGateway;
    private final Timer processingTimer;
    private final DistributionSummary orderAmounts;

    public OrderService(MeterRegistry registry, PaymentGateway paymentGateway) {
        this.registry = registry;
        this.paymentGateway = paymentGateway;

        // Timer: created once, reused for every order. Metric name uses dots; Prometheus turns them into underscores.
        this.processingTimer = Timer.builder("shop.orders.processing")
                .description("Time taken to process (pay) an order")
                .publishPercentileHistogram()          // lets Prometheus/Grafana calculate p95 / p99
                .register(registry);

        this.orderAmounts = DistributionSummary.builder("shop.orders.amount")
                .description("Order value")
                .baseUnit("euros")
                .register(registry);

        // Gauge: Micrometer READS the value whenever someone asks (it keeps a reference to "this").
        Gauge.builder("shop.orders.pending", this, OrderService::countPending)
                .description("Orders waiting for payment right now")
                .register(registry);
    }

    public Order placeOrder(String product, BigDecimal amount, String paymentMethod) {
        // Timer.record(...) measures how long the lambda takes and records it
        Order result = processingTimer.record(() -> {
            long id = ids.incrementAndGet();
            log.debug("Placing order {} for {} ({} via {})", id, product, amount, paymentMethod);
            simulateWork();

            OrderStatus status = paymentGateway.isUp() ? OrderStatus.PAID : OrderStatus.PENDING;
            Order order = new Order(id, product, amount, paymentMethod, status, LocalDateTime.now());
            orders.put(id, order);
            return order;
        });

        // Counter WITH A TAG: one counter per payment method -> you can ask "how many orders were paid by UPI?"
        // Keep tag values to a SMALL fixed set (never user ids or emails - see lesson "cardinality").
        Counter.builder("shop.orders.placed")
                .description("Number of orders placed")
                .tag("paymentMethod", normalise(paymentMethod))
                .tag("status", result.status().name())
                .register(registry)       // returns the existing counter if it was already registered
                .increment();

        orderAmounts.record(amount.doubleValue());

        log.info("Order {} placed: {} - status {}", result.id(), product, result.status());
        return result;
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public long countPending() {
        return orders.values().stream().filter(o -> o.status() == OrderStatus.PENDING).count();
    }

    public long countAll() {
        return orders.size();
    }

    /** Only a few known values become tags - everything else is "other". */
    private static String normalise(String paymentMethod) {
        if (paymentMethod == null) {
            return "other";
        }
        String pm = paymentMethod.toLowerCase();
        return switch (pm) {
            case "card", "upi", "paypal", "cash" -> pm;
            default -> "other";
        };
    }

    private static void simulateWork() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(20, 200));   // 20-200 ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
