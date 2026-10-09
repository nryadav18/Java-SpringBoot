package com.course.patterns.patterns.builder;

import java.util.ArrayList;
import java.util.List;

/*
 * Pizza.java - an IMMUTABLE object created with the BUILDER pattern.
 *
 * Problem: a constructor like new Pizza("LARGE", true, false, true, List.of(...), "thin")
 *          is unreadable ("what was the 3rd boolean?") and needs many overloads.
 * Builder: readable step-by-step creation, optional parts, validation in build():
 *
 *   Pizza pizza = Pizza.builder("LARGE")
 *                      .thinCrust()
 *                      .extraCheese()
 *                      .topping("mushroom")
 *                      .build();
 *
 * You use builders in Spring all the time: RestClient.builder(), ResponseEntity.status(..).body(..),
 * UriComponentsBuilder, MockMvcRequestBuilders, SpringApplicationBuilder, Lombok's @Builder.
 */
public final class Pizza {

    private final String size;
    private final boolean thinCrust;
    private final boolean extraCheese;
    private final List<String> toppings;

    // private: the ONLY way to create a Pizza is through the Builder
    private Pizza(Builder builder) {
        this.size = builder.size;
        this.thinCrust = builder.thinCrust;
        this.extraCheese = builder.extraCheese;
        this.toppings = List.copyOf(builder.toppings); // defensive, unmodifiable copy
    }

    public static Builder builder(String size) {
        return new Builder(size);
    }

    public String size() {
        return size;
    }

    public List<String> toppings() {
        return toppings;
    }

    @Override
    public String toString() {
        return size + " pizza, " + (thinCrust ? "thin" : "regular") + " crust"
                + (extraCheese ? ", extra cheese" : "") + ", toppings " + toppings;
    }

    /** The builder collects the values, then build() creates the finished object. */
    public static final class Builder {
        private static final int MAX_TOPPINGS = 5;

        private final String size;               // required -> constructor parameter
        private boolean thinCrust;               // optional -> methods
        private boolean extraCheese;
        private final List<String> toppings = new ArrayList<>();

        private Builder(String size) {
            this.size = size;
        }

        public Builder thinCrust() {
            this.thinCrust = true;
            return this;                          // returning "this" enables chaining
        }

        public Builder extraCheese() {
            this.extraCheese = true;
            return this;
        }

        public Builder topping(String topping) {
            this.toppings.add(topping);
            return this;
        }

        public Pizza build() {
            if (!List.of("SMALL", "MEDIUM", "LARGE").contains(size)) {
                throw new IllegalStateException("Unknown size: " + size);
            }
            if (toppings.size() > MAX_TOPPINGS) {
                throw new IllegalStateException("Max " + MAX_TOPPINGS + " toppings, got " + toppings.size());
            }
            return new Pizza(this);
        }
    }
}
