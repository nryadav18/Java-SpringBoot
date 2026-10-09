/*
 * @EnableAspectJAutoProxy - switches Spring AOP on.
 *
 * It registers a special BeanPostProcessor (Topic 25!) that, after each bean is
 * initialised, checks: "does any @Aspect pointcut match a method of this bean?"
 * If yes, it returns a PROXY that wraps the bean instead of the bean itself.
 *
 * proxyTargetClass = false (the plain-Spring default):
 *     bean implements an interface -> JDK dynamic proxy  (implements the same interfaces)
 *     bean implements no interface -> CGLIB proxy        (a generated subclass)
 * proxyTargetClass = true: always CGLIB (Spring Boot's default).
 *
 * Spring Boot turns AOP on automatically when spring-boot-starter-aop is present.
 */
package com.course.aop.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.course.aop")
@EnableAspectJAutoProxy
public class AppConfig {
}
