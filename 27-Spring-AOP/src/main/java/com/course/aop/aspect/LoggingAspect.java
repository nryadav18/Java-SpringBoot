/*
 * An ASPECT = a class that holds cross-cutting code ("advice") plus WHERE it applies ("pointcuts").
 *
 * @Aspect     -> "this class is an aspect" (from AspectJ annotations)
 * @Component  -> it must ALSO be a Spring bean, otherwise Spring never sees it!
 * @Order(2)   -> runs INSIDE PerformanceAspect (@Order(1) = outermost)
 *
 * Four kinds of advice shown here:
 *   @Before          - before the method runs
 *   @AfterReturning  - after it returns normally (can read the return value)
 *   @AfterThrowing   - after it throws an exception (can read the exception)
 *   @After           - after it finishes either way (like "finally")
 */
package com.course.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Order(2)
public class LoggingAspect {

    @Before("com.course.aop.aspect.CommonPointcuts.businessLayer()")
    public void logBefore(JoinPoint joinPoint) {
        // JoinPoint = information about the intercepted method call
        System.out.println("   [LOG  @Before]         " + methodName(joinPoint)
                + " args=" + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "com.course.aop.aspect.CommonPointcuts.businessLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("   [LOG  @AfterReturning] " + methodName(joinPoint) + " returned " + result);
    }

    @AfterThrowing(pointcut = "com.course.aop.aspect.CommonPointcuts.businessLayer()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        System.out.println("   [LOG  @AfterThrowing]  " + methodName(joinPoint) + " threw "
                + ex.getClass().getSimpleName() + ": " + ex.getMessage());
    }

    @After("com.course.aop.aspect.CommonPointcuts.businessLayer()")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("   [LOG  @After]          " + methodName(joinPoint) + " finished (success or not)");
    }

    private String methodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();
    }
}
