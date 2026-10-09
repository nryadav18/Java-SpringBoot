/*
 * A CUSTOM ANNOTATION used as a "marker" for AOP.
 *
 * Put @LogExecutionTime on any method of a Spring bean and PerformanceAspect will
 * measure how long it takes - no timing code inside the method itself.
 *
 * @Retention(RUNTIME) is essential: Spring AOP reads the annotation at runtime.
 * (With the default CLASS retention it would be invisible and nothing would happen.)
 */
package com.course.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
