/*
 * NAMED, REUSABLE POINTCUTS.
 *
 * A pointcut is a "WHERE" expression: which method executions should advice run on?
 * Declaring them once here (with @Pointcut on empty methods) lets every aspect reuse
 * them by name, e.g.  @Before("com.course.aop.aspect.CommonPointcuts.serviceLayer()").
 *
 * Expression cheat sheet (all used or explained in the lesson):
 *   execution(* com.course.aop.service.*.*(..))   any method of any class in package "service"
 *   execution(* com.course.aop..*(..))            any method in com.course.aop or sub-packages
 *   execution(public String place*(..))           public methods returning String whose name starts with "place"
 *   within(com.course.aop.payment..*)             any method of classes inside the payment package
 *   @annotation(com.course.aop.annotation.LogExecutionTime)   methods carrying that annotation
 *   bean(orderService)                            all methods of the bean named "orderService" (Spring-only)
 *   combine with  &&  ||  !
 */
package com.course.aop.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointcuts {

    /** Every method in the service and payment packages - our "business layer". */
    @Pointcut("execution(* com.course.aop.service.*.*(..)) || within(com.course.aop.payment..*)")
    public void businessLayer() {
    }

    /** Methods annotated with our custom @LogExecutionTime. */
    @Pointcut("@annotation(com.course.aop.annotation.LogExecutionTime)")
    public void timedMethods() {
    }

    /** Only methods whose name starts with "place" (placeOrder, placeBulkOrders). */
    @Pointcut("execution(* com.course.aop.service.*.place*(..))")
    public void placingOrders() {
    }
}
