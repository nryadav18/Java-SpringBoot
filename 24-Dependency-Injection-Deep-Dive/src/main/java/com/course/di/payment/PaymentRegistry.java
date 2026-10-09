/*
 * Injecting ALL beans of a type at once.
 *
 *   List<PaymentGateway>         -> every PaymentGateway bean, sorted by @Order
 *   Map<String, PaymentGateway>  -> every PaymentGateway bean, KEY = bean name
 *
 * This is the clean way to build a "strategy picker": add a new payment class
 * with @Component and it automatically appears here - no if/else to update.
 */
package com.course.di.payment;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PaymentRegistry {

    private final List<PaymentGateway> allGateways;
    private final Map<String, PaymentGateway> gatewaysByName;

    public PaymentRegistry(List<PaymentGateway> allGateways, Map<String, PaymentGateway> gatewaysByName) {
        this.allGateways = allGateways;
        this.gatewaysByName = gatewaysByName;
    }

    /** The options to show on the checkout page, in @Order order. */
    public List<String> availableMethods() {
        return allGateways.stream().map(PaymentGateway::displayName).toList();
    }

    public java.util.Set<String> beanNames() {
        return gatewaysByName.keySet();
    }

    /** Pick a gateway by its bean name, e.g. "upiPayment" or "paypal". */
    public String payWith(String beanName, double amount) {
        PaymentGateway gateway = gatewaysByName.get(beanName);
        if (gateway == null) {
            throw new IllegalArgumentException("Unknown payment method: " + beanName);
        }
        return gateway.pay(amount);
    }
}
