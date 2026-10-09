/*
 * ============================================================
 *  Topic 3 - Example 03 : Loops - for, while, do-while, enhanced for
 * ============================================================
 *  What this file demonstrates:
 *    - for loop: when you know HOW MANY times to repeat.
 *    - Counting down and stepping by more than 1.
 *    - while loop: repeat WHILE a condition is true (count unknown in advance).
 *    - do-while loop: runs the body AT LEAST ONCE, checks afterwards.
 *    - enhanced for (for-each): go through every item of an array.
 *
 *  How to run:
 *    java Example03_Loops.java
 * ============================================================
 */
public class Example03_Loops {

    public static void main(String[] args) {

        // ---------- 1. for loop ----------
        // for (start; condition; update) { body }
        //  1) start runs once
        //  2) condition is checked - if false, the loop ends
        //  3) body runs
        //  4) update runs, then back to step 2
        System.out.print("Count 1 to 5: ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // Counting down
        System.out.print("Rocket launch: ");
        for (int i = 5; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println("LIFT OFF!");

        // Stepping by 2 (even numbers)
        System.out.print("Even numbers up to 10: ");
        for (int i = 2; i <= 10; i += 2) {
            System.out.print(i + " ");
        }
        System.out.println();

        // ---------- 2. while loop ----------
        // Use when you don't know in advance how many times.
        // Example: how many years until savings double at 8% interest?
        double savings = 10000;
        int years = 0;
        while (savings < 20000) {
            savings = savings * 1.08;   // add 8% interest
            years++;
        }
        System.out.printf("Savings double after %d years (%.2f)%n", years, savings);

        // Digits of a number with while
        int number = 90210;
        int digitCount = 0;
        int temp = number;
        while (temp > 0) {
            temp = temp / 10;           // chop off the last digit
            digitCount++;
        }
        System.out.println(number + " has " + digitCount + " digits");

        // ---------- 3. do-while loop ----------
        // The body runs FIRST, the condition is checked AFTER. So it always runs at least once.
        // Perfect for menus: "show the menu, then ask if the user wants to continue".
        int attempts = 0;
        do {
            attempts++;
            System.out.println("Attempt " + attempts + ": trying to connect...");
        } while (attempts < 3);

        // Even if the condition is false from the start, do-while runs once:
        int x = 100;
        do {
            System.out.println("do-while ran once even though x < 10 is false");
        } while (x < 10);

        // The same with while runs ZERO times:
        while (x < 10) {
            System.out.println("You will never see this line");
        }

        // ---------- 4. enhanced for (for-each) ----------
        // Goes through each element of an array (or collection) one by one.
        // Read it as: "for each price in prices".
        double[] prices = {120.0, 45.5, 300.0, 15.25};
        double total = 0;
        for (double price : prices) {
            total += price;
        }
        System.out.println("Cart total: " + total);

        String[] fruits = {"Apple", "Mango", "Banana"};
        for (String fruit : fruits) {
            System.out.println("- " + fruit);
        }
    }
}

// Output:
// Count 1 to 5: 1 2 3 4 5
// Rocket launch: 5 4 3 2 1 LIFT OFF!
// Even numbers up to 10: 2 4 6 8 10
// Savings double after 10 years (21589.25)
// 90210 has 5 digits
// Attempt 1: trying to connect...
// Attempt 2: trying to connect...
// Attempt 3: trying to connect...
// do-while ran once even though x < 10 is false
// Cart total: 480.75
// - Apple
// - Mango
// - Banana
