/*
 * The configuration class: tells Spring WHERE to find beans and HOW to build special ones.
 *
 * @Configuration   -> this class holds bean definitions (it is itself a bean, too).
 * @ComponentScan   -> "scan this package AND all sub-packages for classes annotated with
 *                     @Component / @Service / @Repository / @Controller / @Configuration
 *                     and create a bean for each one."
 *
 * @Bean methods    -> for objects we CANNOT annotate (JDK or third-party classes such as
 *                     Clock and NumberFormat), or that need special construction code.
 *
 * Rule of thumb:
 *   your own classes         -> @Component (and friends) + component scanning
 *   classes you don't own    -> @Bean method in a @Configuration class
 */
package com.course.ioccontainer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.text.NumberFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Locale;

@Configuration
@ComponentScan(basePackages = "com.course.ioccontainer")   // scan our whole app package
public class AppConfig {

    /**
     * A Clock bean. We use a FIXED clock so the demo output is always the same
     * (and tests become predictable). In production you would return Clock.systemDefaultZone().
     * Bean name = method name = "clock".
     */
    @Bean
    public Clock clock() {
        System.out.println("   [container] @Bean method clock() called");
        return Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneOffset.UTC);
    }

    /**
     * A NumberFormat bean with a CUSTOM name: "moneyFormat" (instead of the method name).
     * You can even give several names (aliases): @Bean(name = {"moneyFormat", "currencyFormat"}).
     */
    @Bean(name = {"moneyFormat", "currencyFormat"})
    public NumberFormat usDollarFormat() {
        System.out.println("   [container] @Bean method usDollarFormat() called");
        return NumberFormat.getCurrencyInstance(Locale.US);
    }
}
