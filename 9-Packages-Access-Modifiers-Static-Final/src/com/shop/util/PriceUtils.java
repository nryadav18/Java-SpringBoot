/*
 * File: src/com/shop/util/PriceUtils.java
 * Package: com.shop.util
 *
 * What this file demonstrates:
 *   - A classic "utility class" (like java.lang.Math):
 *       * "final class"         -> nobody can extend it
 *       * private constructor   -> nobody can create an object of it
 *       * only static members   -> you call PriceUtils.withGst(100) directly
 *   - A constant: "public static final" (one shared copy, never changes).
 *     Constant names are written in UPPER_SNAKE_CASE by convention.
 *
 * How to run: no main() here. Used by com.shop.App.
 */
package com.shop.util;

public final class PriceUtils {

    // static  -> belongs to the CLASS (one copy for the whole program)
    // final   -> can never be re-assigned
    // public  -> anyone may read it
    public static final double GST_RATE = 0.18;   // 18% tax

    // private constructor: "new PriceUtils()" is impossible from outside.
    // A utility class only holds helpers, so objects of it make no sense.
    private PriceUtils() {
    }

    public static double withGst(double amount) {
        return amount + amount * GST_RATE;
    }

    public static String formatRupees(double amount) {
        // %.2f = 2 digits after the decimal point
        return String.format("Rs.%.2f", amount);
    }
}
