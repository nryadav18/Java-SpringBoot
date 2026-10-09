/*
 * @Component("lateFeeCalculator") - a generic Spring-managed helper with a CUSTOM bean name.
 *
 * @Component is the most general stereotype: "Spring, please create and manage this class".
 * Without the "lateFeeCalculator" value the bean name would be "fineCalculator".
 *
 * It needs a NumberFormat to print money nicely. That NumberFormat is NOT our class
 * (it comes from the JDK), so we cannot put @Component on it - it is defined with
 * @Bean in AppConfig instead. Spring injects it here through the constructor.
 */
package com.course.ioccontainer.service;

import org.springframework.stereotype.Component;

import java.text.NumberFormat;

@Component("lateFeeCalculator")
public class FineCalculator {

    private static final double FINE_PER_DAY = 0.50;

    private final NumberFormat moneyFormat;

    // Only one constructor -> Spring uses it automatically and injects the NumberFormat bean.
    public FineCalculator(NumberFormat moneyFormat) {
        System.out.println("   [container] creating FineCalculator (bean name: lateFeeCalculator)");
        this.moneyFormat = moneyFormat;
    }

    public String fineFor(long daysLate) {
        return moneyFormat.format(daysLate * FINE_PER_DAY);
    }
}
