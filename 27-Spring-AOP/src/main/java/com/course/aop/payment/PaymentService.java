/*
 * An interface for payments. Because the bean (CardPaymentService) implements an
 * interface, Spring (with its plain-Spring default settings) creates a JDK DYNAMIC PROXY:
 * a generated class that implements PaymentService and forwards calls to the real bean.
 */
package com.course.aop.payment;

public interface PaymentService {

    String pay(String orderId, double amount);
}
