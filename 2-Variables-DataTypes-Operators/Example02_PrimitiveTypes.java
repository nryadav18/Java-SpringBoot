/*
 * ============================================================
 *  Topic 2 - Example 02 : The 8 Primitive Types
 * ============================================================
 *  What this file demonstrates:
 *    - byte, short, int, long, float, double, char, boolean.
 *    - Their smallest and largest values (using wrapper class constants).
 *    - Literal suffixes: L for long, f for float. Underscores in numbers.
 *    - char is really a number (Unicode code).
 *    - double is not perfectly precise (0.1 + 0.2).
 *
 *  How to run:
 *    java Example02_PrimitiveTypes.java
 * ============================================================
 */
public class Example02_PrimitiveTypes {

    public static void main(String[] args) {

        // ---------- whole numbers (integers) ----------
        byte age = 25;                       // 1 byte : -128 to 127
        short pinCode = 30_000;              // 2 bytes: -32,768 to 32,767
        int population = 1_400_000_000;      // 4 bytes: about -2.1 billion to 2.1 billion (DEFAULT for whole numbers)
        long worldPopulation = 8_100_000_000L; // 8 bytes: huge. Note the L at the end!
        // The underscores _ are only for readability; Java ignores them.

        System.out.println("byte  age             = " + age);
        System.out.println("short pinCode         = " + pinCode);
        System.out.println("int   population      = " + population);
        System.out.println("long  worldPopulation = " + worldPopulation);

        // ---------- decimal numbers (floating point) ----------
        float rating = 4.5f;                 // 4 bytes, ~7 digits precision. Note the f!
        double accountBalance = 12345.6789;  // 8 bytes, ~15-16 digits precision (DEFAULT for decimals)
        System.out.println("float  rating         = " + rating);
        System.out.println("double accountBalance = " + accountBalance);

        // ---------- characters ----------
        char letter = 'J';                   // 2 bytes, one Unicode character, single quotes
        char rupee = '\u20B9';             // Unicode escape (backslash-u + hex code) for the Rupee sign
        System.out.println("char letter = " + letter);
        System.out.println("char code of 'J' = " + (int) letter); // a char is stored as a number
        System.out.println("rupee as code = " + (int) rupee);

        // ---------- true / false ----------
        boolean isJavaFun = true;            // only true or false (not 0/1 like some languages)
        System.out.println("boolean isJavaFun = " + isJavaFun);

        // ---------- ranges (min and max) ----------
        // Each primitive has a "wrapper class" (Byte, Short, Integer...) with handy constants.
        System.out.println();
        System.out.println("byte  range: " + Byte.MIN_VALUE + " to " + Byte.MAX_VALUE);
        System.out.println("short range: " + Short.MIN_VALUE + " to " + Short.MAX_VALUE);
        System.out.println("int   range: " + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE);
        System.out.println("long  range: " + Long.MIN_VALUE + " to " + Long.MAX_VALUE);
        System.out.println("float max  : " + Float.MAX_VALUE);
        System.out.println("double max : " + Double.MAX_VALUE);
        System.out.println("char range : " + (int) Character.MIN_VALUE + " to " + (int) Character.MAX_VALUE);

        // ---------- a famous surprise ----------
        // double stores numbers in binary, and 0.1 cannot be stored exactly in binary.
        System.out.println();
        System.out.println("0.1 + 0.2 = " + (0.1 + 0.2));   // not exactly 0.3!
        // That's why money is usually handled with BigDecimal in real banking apps (see the lesson).
    }
}

// Output:
// byte  age             = 25
// short pinCode         = 30000
// int   population      = 1400000000
// long  worldPopulation = 8100000000
// float  rating         = 4.5
// double accountBalance = 12345.6789
// char letter = J
// char code of 'J' = 74
// rupee as code = 8377
// boolean isJavaFun = true
//
// byte  range: -128 to 127
// short range: -32768 to 32767
// int   range: -2147483648 to 2147483647
// long  range: -9223372036854775808 to 9223372036854775807
// float max  : 3.4028235E38
// double max : 1.7976931348623157E308
// char range : 0 to 65535
//
// 0.1 + 0.2 = 0.30000000000000004
