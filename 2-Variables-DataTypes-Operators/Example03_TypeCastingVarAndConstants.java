/*
 * ============================================================
 *  Topic 2 - Example 03 : Type Casting, var and Constants
 * ============================================================
 *  What this file demonstrates:
 *    - Widening (automatic, safe) conversion: small type -> big type.
 *    - Narrowing (manual cast, may lose data): big type -> small type.
 *    - Overflow: what happens when a number is too big for its type.
 *    - char <-> int conversion.
 *    - Converting text to numbers (Integer.parseInt) and numbers to text.
 *    - 'var' (local variable type inference, Java 10+).
 *    - Constants with 'final'.
 *
 *  How to run:
 *    java Example03_TypeCastingVarAndConstants.java
 * ============================================================
 */
public class Example03_TypeCastingVarAndConstants {

    // A CONSTANT: 'static final' at class level. Name in UPPER_SNAKE_CASE by convention.
    // 'final' means: once given a value, it can never change.
    static final double GST_RATE = 0.18;
    static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {

        // ---------- 1. WIDENING (automatic) ----------
        // Pouring a small glass into a big bucket: nothing spills, so Java does it for you.
        // byte -> short -> int -> long -> float -> double
        int itemsSold = 150;
        long bigTotal = itemsSold;          // int fits easily in long
        double asDecimal = itemsSold;       // int -> double, becomes 150.0
        System.out.println("Widening: int " + itemsSold + " -> long " + bigTotal + " -> double " + asDecimal);

        // ---------- 2. NARROWING (manual cast) ----------
        // Pouring a big bucket into a small glass: something may spill.
        // Java forces you to write (type) to show "I know what I'm doing".
        double price = 99.99;
        int roundedDown = (int) price;      // decimal part is CUT OFF (not rounded!)
        System.out.println("Narrowing: double " + price + " -> int " + roundedDown);

        long hugeNumber = 3_000_000_000L;
        int squeezed = (int) hugeNumber;    // too big for int -> wraps around to a strange number
        System.out.println("Narrowing overflow: long " + hugeNumber + " -> int " + squeezed);

        // If you want proper rounding, use Math.round:
        System.out.println("Math.round(99.99) = " + Math.round(price));

        // ---------- 3. OVERFLOW ----------
        int maxInt = Integer.MAX_VALUE;     // 2147483647
        int overflowed = maxInt + 1;        // goes past the end and wraps to the most negative int!
        System.out.println("Overflow: " + maxInt + " + 1 = " + overflowed);

        // ---------- 4. char <-> int ----------
        char letter = 'A';
        int code = letter;                  // widening: char -> int gives the Unicode number
        char next = (char) (code + 1);      // narrowing: int -> char
        System.out.println("'A' has code " + code + ", next letter is " + next);

        // ---------- 5. Text <-> numbers ----------
        String quantityText = "42";                    // text that looks like a number
        int quantity = Integer.parseInt(quantityText); // convert text -> int
        double amount = Double.parseDouble("19.5");    // convert text -> double
        System.out.println("Parsed: " + (quantity + 1) + " and " + (amount * 2));

        String backToText = String.valueOf(quantity);  // number -> text
        System.out.println("As text again: " + backToText + "!");

        // ---------- 6. var (Java 10+) ----------
        // The compiler looks at the right side and figures out the type for you.
        var city = "Pune";        // compiler infers String
        var year = 2026;          // compiler infers int
        var temperature = 31.5;   // compiler infers double
        // city = 10;             // ERROR: city is still a String. var is NOT "any type".
        System.out.println("var: " + city + ", " + year + ", " + temperature);

        // ---------- 7. Constants with final ----------
        final int daysInWeek = 7;           // a local constant
        // daysInWeek = 8;                  // ERROR: cannot assign a value to final variable
        double billAmount = 1000;
        double gst = billAmount * GST_RATE;
        System.out.println("Days in week: " + daysInWeek);
        System.out.println("GST on 1000 = " + gst);
        System.out.println("Max login attempts: " + MAX_LOGIN_ATTEMPTS);
    }
}

// Output:
// Widening: int 150 -> long 150 -> double 150.0
// Narrowing: double 99.99 -> int 99
// Narrowing overflow: long 3000000000 -> int -1294967296
// Math.round(99.99) = 100
// Overflow: 2147483647 + 1 = -2147483648
// 'A' has code 65, next letter is B
// Parsed: 43 and 39.0
// As text again: 42!
// var: Pune, 2026, 31.5
// Days in week: 7
// GST on 1000 = 180.0
// Max login attempts: 3
