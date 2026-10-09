/*
 * CONSTRUCTOR INJECTION (the recommended style) + @Primary in action.
 *
 * - Dependencies are final fields set in the constructor -> the object is always complete.
 * - Only ONE constructor, so @Autowired is OPTIONAL (Spring 4.3+ uses it automatically).
 * - We ask for "a PaymentGateway" without saying which. Three exist, but CardPayment is
 *   marked @Primary, so Spring injects the card gateway.
 */
package com.course.di.order;

import com.course.di.config.ShopSettings;
import com.course.di.payment.PaymentGateway;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

    private final PaymentGateway paymentGateway;
    private final ShopSettings shopSettings;

    // No @Autowired needed: single constructor.
    public CheckoutService(PaymentGateway paymentGateway, ShopSettings shopSettings) {
        this.paymentGateway = paymentGateway;
        this.shopSettings = shopSettings;
    }

    public String checkout(double foodTotal) {
        double total = foodTotal + shopSettings.deliveryFeeFor(foodTotal);
        return paymentGateway.pay(total);
    }

    public String gatewayInUse() {
        return paymentGateway.displayName();
    }
}
