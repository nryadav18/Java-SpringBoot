/*
 * FULL mode:  @Configuration   (same as @Configuration(proxyBeanMethods = true), the default)
 *
 * Spring creates a CGLIB SUBCLASS (proxy) of this class at startup. That subclass
 * overrides auditLog() so that calling it from another @Bean method returns the
 * EXISTING singleton from the container instead of running "new AuditLog()" again.
 *
 * Result: orderService and paymentService SHARE ONE AuditLog (#1).
 */
package com.course.configprofiles.proxydemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FullConfig {

    @Bean
    public AuditLog auditLog() {
        return new AuditLog();
    }

    @Bean
    public AuditedService orderService() {
        return new AuditedService("orderService", auditLog());     // direct method call!
    }

    @Bean
    public AuditedService paymentService() {
        return new AuditedService("paymentService", auditLog());   // direct method call!
    }
}
