/*
 * An aspect that can BLOCK a method call.
 *
 * Pointcut with ARGUMENT BINDING:  args(item, quantity)
 *   -> matches only methods whose arguments fit (String, int), AND hands those
 *      argument values to the advice method as typed parameters. No casting of Object[] needed.
 *
 * If a @Before advice throws an exception, the real method NEVER runs.
 * (Spring Security's method security works on this same idea - Topic 48.)
 *
 * @Order(3): runs INSIDE LoggingAspect (@Order(2)), so the logging aspect still sees
 * the call start and the exception.
 */
package com.course.aop.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(3)
public class ValidationAspect {

    private static final int MAX_QUANTITY = 100;

    @Before("com.course.aop.aspect.CommonPointcuts.placingOrders() && args(item, quantity)")
    public void checkQuantity(String item, int quantity) {
        if (quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException(
                    "Quantity " + quantity + " too large for " + item + " (max " + MAX_QUANTITY + ")");
        }
    }
}
