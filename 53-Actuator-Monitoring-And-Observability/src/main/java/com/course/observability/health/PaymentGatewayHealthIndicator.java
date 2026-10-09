package com.course.observability.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/*
 * A CUSTOM HEALTH INDICATOR.
 *
 * Spring Boot already checks disk space, database, Redis ... automatically.
 * For our own important dependencies we write a HealthIndicator.
 *
 * The bean name decides the name in /actuator/health:
 *   class "PaymentGatewayHealthIndicator" -> component "paymentGateway"
 *
 * If ANY component is DOWN, the overall status becomes DOWN and /actuator/health returns HTTP 503.
 * Load balancers and Kubernetes use exactly this to stop sending traffic to a sick instance.
 *
 * Keep health checks FAST and CHEAP - they may be called every few seconds.
 */
@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {

    private final PaymentGateway paymentGateway;

    public PaymentGatewayHealthIndicator(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Override
    public Health health() {
        try {
            long latencyMs = paymentGateway.ping();
            return Health.up()
                    .withDetail("provider", "DemoPay")
                    .withDetail("latencyMs", latencyMs)
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("provider", "DemoPay")
                    .withDetail("error", ex.getMessage())   // a short message - NEVER passwords or stack traces
                    .build();
        }
    }
}
