/*
 * SpEL - the Spring Expression Language:  #{ ... }
 *
 * ${...}  = "look up a property"           (simple text replacement)
 * #{...}  = "evaluate an expression"       (maths, method calls, other beans, conditions)
 *
 * They can be combined: #{${shop.base-delivery-fee} * 1.18}
 *   step 1: ${...} is replaced first  -> #{50 * 1.18}
 *   step 2: SpEL evaluates it         -> 59.0
 */
package com.course.configprofiles.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PricingRules {

    @Value("#{2 * 60 * 60}")                                   // arithmetic
    private int sessionTimeoutSeconds;

    @Value("#{'${shop.name}'.toUpperCase()}")                  // property + method call on a String
    private String shopNameUpper;

    @Value("#{${shop.base-delivery-fee} * 1.18}")              // property + arithmetic (add 18% tax)
    private double deliveryFeeWithTax;

    @Value("#{${shop.max-items} > 20 ? 'BULK' : 'NORMAL'}")    // ternary operator
    private String orderMode;

    @Value("#{T(java.lang.Math).max(10, ${shop.max-items})}")  // T(...) = call a static method
    private int effectiveLimit;

    @Value("#{storageService.name()}")                         // call a method on ANOTHER BEAN by its name
    private String storageName;

    @Value("#{systemProperties['java.version']}")              // built-in variable: system properties
    private String javaVersion;

    @Value("#{ {100, 250, 499, 1200}.?[#this > 300] }")        // collection selection: .?[condition]
    private List<Integer> bigOrderAmounts;

    public void print() {
        System.out.println("   #{2 * 60 * 60}                     -> " + sessionTimeoutSeconds);
        System.out.println("   #{'${shop.name}'.toUpperCase()}    -> " + shopNameUpper);
        System.out.println("   #{${shop.base-delivery-fee} * 1.18} -> " + deliveryFeeWithTax);
        System.out.println("   #{${shop.max-items} > 20 ? ...}     -> " + orderMode);
        System.out.println("   #{T(java.lang.Math).max(...)}      -> " + effectiveLimit);
        System.out.println("   #{storageService.name()}           -> " + storageName);
        System.out.println("   #{systemProperties['java.version']} -> " + javaVersion);
        System.out.println("   #{ {100,250,499,1200}.?[#this > 300] } -> " + bigOrderAmounts);
    }
}
