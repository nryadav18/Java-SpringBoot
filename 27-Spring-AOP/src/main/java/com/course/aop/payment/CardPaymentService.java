/*
 * Implementation of PaymentService. Also marked @LogExecutionTime to show that the
 * custom-annotation pointcut works with JDK proxies too.
 */
package com.course.aop.payment;

import com.course.aop.annotation.LogExecutionTime;
import org.springframework.stereotype.Service;

@Service
public class CardPaymentService implements PaymentService {

    @Override
    @LogExecutionTime
    public String pay(String orderId, double amount) {
        System.out.println("      (business logic) charging card " + amount + " for " + orderId);
        return "PAID-" + orderId;
    }
}
