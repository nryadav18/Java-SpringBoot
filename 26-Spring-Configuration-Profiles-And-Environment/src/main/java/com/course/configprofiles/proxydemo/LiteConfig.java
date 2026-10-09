/*
 * LITE mode:  @Configuration(proxyBeanMethods = false)
 *
 * No CGLIB subclass is created. auditLog() is just a normal Java method, so every
 * direct call runs "new AuditLog()" again -> the services get their OWN extra AuditLogs,
 * which are NOT the container's singleton. (Startup is a little faster - Spring Boot's
 * own auto-configuration classes use this mode.)
 *
 * The safe way in lite mode: ask for the dependency as a METHOD PARAMETER - see
 * safePaymentService(). Parameters are always resolved from the container.
 */
package com.course.configprofiles.proxydemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class LiteConfig {

    @Bean
    public AuditLog auditLog() {
        return new AuditLog();
    }

    @Bean
    public AuditedService orderService() {
        return new AuditedService("orderService", auditLog());     // plain call -> NEW AuditLog
    }

    @Bean
    public AuditedService paymentService() {
        return new AuditedService("paymentService", auditLog());   // plain call -> another NEW one
    }

    @Bean
    public AuditedService safePaymentService(AuditLog auditLog) {  // parameter -> the real singleton
        return new AuditedService("safePaymentService", auditLog);
    }
}
