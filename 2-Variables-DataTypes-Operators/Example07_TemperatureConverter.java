/*
 * ============================================================
 *  Topic 2 - Example 07 : Mini-program - Temperature Converter (with input)
 * ============================================================
 *  What this file demonstrates:
 *    - Reading a decimal number from the user (Scanner, from Topic 1).
 *    - Using formulas with correct decimal arithmetic.
 *    - The integer-division trap (5 / 9 is 0!) and how to avoid it.
 *
 *  Formulas:
 *    Fahrenheit = Celsius * 9 / 5 + 32
 *    Celsius    = (Fahrenheit - 32) * 5 / 9
 *    Kelvin     = Celsius + 273.15
 *
 *  How to run:
 *    java Example07_TemperatureConverter.java
 *  Then type a temperature in Celsius, e.g. 37
 * ============================================================
 */
import java.util.Scanner;

public class Example07_TemperatureConverter {

    static final double KELVIN_OFFSET = 273.15;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter temperature in Celsius: ");
        double celsius = scanner.nextDouble();

        // 9.0 / 5.0 (decimals!) - NOT 9 / 5, which would be integer division = 1.
        double fahrenheit = celsius * 9.0 / 5.0 + 32;
        double kelvin = celsius + KELVIN_OFFSET;

        System.out.printf("%.2f C = %.2f F%n", celsius, fahrenheit);
        System.out.printf("%.2f C = %.2f K%n", celsius, kelvin);

        // And back again, to check our formula:
        double backToCelsius = (fahrenheit - 32) * 5.0 / 9.0;
        System.out.printf("Check: %.2f F = %.2f C%n", fahrenheit, backToCelsius);

        // The classic bug, shown on purpose:
        double wrong = (fahrenheit - 32) * (5 / 9);   // 5 / 9 is int division = 0 -> always 0!
        System.out.println("Buggy formula with (5 / 9) gives: " + wrong);

        scanner.close();
    }
}

// Output (when you type 37):
// Enter temperature in Celsius: 37
// 37.00 C = 98.60 F
// 37.00 C = 310.15 K
// Check: 98.60 F = 37.00 C
// Buggy formula with (5 / 9) gives: 0.0
