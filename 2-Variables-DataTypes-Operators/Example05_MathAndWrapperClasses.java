/*
 * ============================================================
 *  Topic 2 - Example 05 : Math class and Wrapper classes
 * ============================================================
 *  What this file demonstrates:
 *    - Useful Math methods: max, min, abs, pow, sqrt, round, ceil, floor, random.
 *    - Wrapper classes (Integer, Double, Boolean, Character...).
 *    - Autoboxing (primitive -> wrapper) and unboxing (wrapper -> primitive).
 *    - null in wrappers and the danger of unboxing null.
 *    - Comparing wrappers: use equals(), not ==.
 *
 *  How to run:
 *    java Example05_MathAndWrapperClasses.java
 * ============================================================
 */
public class Example05_MathAndWrapperClasses {

    public static void main(String[] args) {

        // ---------- 1. The Math class ----------
        // Math is a built-in class full of ready-made maths methods. No import needed (java.lang).
        System.out.println("--- Math ---");
        System.out.println("Math.max(10, 25)   = " + Math.max(10, 25));
        System.out.println("Math.min(10, 25)   = " + Math.min(10, 25));
        System.out.println("Math.abs(-7)       = " + Math.abs(-7));       // distance from 0
        System.out.println("Math.pow(2, 10)    = " + Math.pow(2, 10));    // 2 to the power 10 (returns double)
        System.out.println("Math.sqrt(144)     = " + Math.sqrt(144));     // square root
        System.out.println("Math.round(4.5)    = " + Math.round(4.5));    // nearest whole number -> 5
        System.out.println("Math.round(4.4)    = " + Math.round(4.4));    // -> 4
        System.out.println("Math.ceil(4.1)     = " + Math.ceil(4.1));     // always UP -> 5.0
        System.out.println("Math.floor(4.9)    = " + Math.floor(4.9));    // always DOWN -> 4.0
        System.out.println("Math.PI            = " + Math.PI);

        // Rounding money to 2 decimals (a common trick):
        double interest = 1234.56789;
        double rounded = Math.round(interest * 100) / 100.0;
        System.out.println("Rounded to 2 decimals: " + rounded);

        // Random whole number between 1 and 6 (like a dice). Math.random() gives 0.0 (incl) to 1.0 (excl).
        int dice = (int) (Math.random() * 6) + 1;
        System.out.println("Dice roll (random 1-6): " + (dice >= 1 && dice <= 6 ? "ok" : "bug"));

        // ---------- 2. Wrapper classes ----------
        // Every primitive has a matching CLASS ("wrapper") that wraps the value in an object:
        // byte->Byte, short->Short, int->Integer, long->Long,
        // float->Float, double->Double, char->Character, boolean->Boolean
        // Why? Collections like List (Topic 11) can store ONLY objects, not primitives.
        System.out.println("--- Wrappers ---");

        // AUTOBOXING: Java automatically wraps a primitive into its wrapper.
        int primitiveAge = 30;
        Integer boxedAge = primitiveAge;          // int -> Integer, automatically
        System.out.println("boxedAge = " + boxedAge);

        // UNBOXING: Java automatically unwraps the wrapper back to a primitive.
        Integer boxedMarks = 88;
        int marks = boxedMarks;                   // Integer -> int, automatically
        System.out.println("marks + 2 = " + (marks + 2));

        // Wrappers have useful helper methods:
        System.out.println("Integer.parseInt(\"250\") + 1 = " + (Integer.parseInt("250") + 1));
        System.out.println("Integer.valueOf(\"99\")    = " + Integer.valueOf("99"));
        System.out.println("Integer.toBinaryString(10) = " + Integer.toBinaryString(10));
        System.out.println("Character.isDigit('7')    = " + Character.isDigit('7'));
        System.out.println("Character.isLetter('7')   = " + Character.isLetter('7'));
        System.out.println("Character.toUpperCase('q')= " + Character.toUpperCase('q'));
        System.out.println("Boolean.parseBoolean(\"TRUE\") = " + Boolean.parseBoolean("TRUE"));

        // ---------- 3. Wrappers can be null ----------
        // A primitive int ALWAYS has a number. An Integer can be null (= "no value").
        Integer discount = null;                  // e.g. "no discount was set"
        System.out.println("discount = " + discount);
        // int d = discount;   // DANGER: unboxing null -> NullPointerException at runtime!

        // ---------- 4. Comparing wrappers ----------
        Integer small1 = 100, small2 = 100;
        Integer big1 = 1000, big2 = 1000;
        System.out.println("small1 == small2      : " + (small1 == small2));     // true (cached -128..127)
        System.out.println("big1 == big2          : " + (big1 == big2));         // false! different objects
        System.out.println("big1.equals(big2)     : " + big1.equals(big2));      // true - compares VALUES
    }
}

// Output:
// --- Math ---
// Math.max(10, 25)   = 25
// Math.min(10, 25)   = 10
// Math.abs(-7)       = 7
// Math.pow(2, 10)    = 1024.0
// Math.sqrt(144)     = 12.0
// Math.round(4.5)    = 5
// Math.round(4.4)    = 4
// Math.ceil(4.1)     = 5.0
// Math.floor(4.9)    = 4.0
// Math.PI            = 3.141592653589793
// Rounded to 2 decimals: 1234.57
// Dice roll (random 1-6): ok
// --- Wrappers ---
// boxedAge = 30
// marks + 2 = 90
// Integer.parseInt("250") + 1 = 251
// Integer.valueOf("99")    = 99
// Integer.toBinaryString(10) = 1010
// Character.isDigit('7')    = true
// Character.isLetter('7')   = false
// Character.toUpperCase('q')= Q
// Boolean.parseBoolean("TRUE") = true
// discount = null
// small1 == small2      : true
// big1 == big2          : false
// big1.equals(big2)     : true
