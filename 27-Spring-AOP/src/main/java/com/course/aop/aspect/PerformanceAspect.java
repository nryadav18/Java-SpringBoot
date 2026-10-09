/*
 * @Around - the most powerful advice: it WRAPS the method call.
 *
 * You get a ProceedingJoinPoint and decide yourself:
 *   - code before
 *   - joinPoint.proceed()  -> actually call the real method (you MUST do this, or the method never runs!)
 *   - code after, using the result
 *   - you could even change arguments, change the return value, retry, or catch exceptions
 *
 * Pointcut: only methods annotated with our custom @LogExecutionTime.
 * @Order(1): the outermost aspect, so the timing includes the logging advice inside it.
 */
package com.course.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class PerformanceAspect {

    @Around("com.course.aop.aspect.CommonPointcuts.timedMethods()")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        System.out.println("   [TIME @Around]  start " + method);
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();            // run the real method (and any inner advice)
        } finally {
            long millis = (System.nanoTime() - start) / 1_000_000;
            System.out.println("   [TIME @Around]  " + method + " took " + millis + " ms");
        }
    }
}
