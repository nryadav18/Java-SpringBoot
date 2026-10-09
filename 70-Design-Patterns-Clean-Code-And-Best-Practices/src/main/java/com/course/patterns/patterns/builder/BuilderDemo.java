package com.course.patterns.patterns.builder;

import com.course.patterns.demo.PatternDemo;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
 * BUILDER (creational pattern) - see Pizza.java for the pattern itself.
 *
 * Expected output:
 *   LARGE pizza, thin crust, extra cheese, toppings [mushroom, olives]
 *   SMALL pizza, regular crust, toppings []
 *   Invalid pizza -> Unknown size: HUGE
 */
@Component
@Order(12)
public class BuilderDemo implements PatternDemo {

    @Override
    public String title() {
        return "Builder - readable creation of complex objects";
    }

    @Override
    public void run() {
        Pizza fancy = Pizza.builder("LARGE")
                .thinCrust()
                .extraCheese()
                .topping("mushroom")
                .topping("olives")
                .build();
        System.out.println(fancy);

        System.out.println(Pizza.builder("SMALL").build());

        try {
            Pizza.builder("HUGE").build();
        } catch (IllegalStateException e) {
            System.out.println("Invalid pizza -> " + e.getMessage());
        }
    }
}
