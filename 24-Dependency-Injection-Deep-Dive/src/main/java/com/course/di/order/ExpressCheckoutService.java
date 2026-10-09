/*
 * @Qualifier - choosing ONE specific bean among several candidates.
 *
 * Express checkout always uses UPI. @Qualifier("upiPayment") names the exact bean.
 * @Qualifier WINS over @Primary: the more specific request beats the general default.
 */
package com.course.di.order;

import com.course.di.payment.PaymentGateway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ExpressCheckoutService {

    private final PaymentGateway paymentGateway;

    public ExpressCheckoutService(@Qualifier("upiPayment") PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String quickPay(double amount) {
        return paymentGateway.pay(amount);
    }

    public String gatewayInUse() {
        return paymentGateway.displayName();
    }
}
