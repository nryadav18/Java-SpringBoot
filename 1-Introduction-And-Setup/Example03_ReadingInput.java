/*
 * ============================================================
 *  Topic 1 - Example 03 : Reading Input with Scanner
 * ============================================================
 *  What this file demonstrates:
 *    - Using an "import" to bring in a ready-made class (Scanner).
 *    - Reading a whole line of text, a whole number and a decimal number from the keyboard.
 *    - Using what the user typed in our output.
 *
 *  How to run:
 *    java Example03_ReadingInput.java
 *  Then type your answers and press Enter after each one.
 *
 *  Sample run (what YOU type is shown after the "> " prompts):
 *    What is your name? > Priya
 *    How old are you? > 25
 *    What is your height in metres? > 1.62
 *    Hello, Priya!
 *    Next year you will be 26 years old.
 *    Your height is 1.62 m.
 * ============================================================
 */

// "import" tells Java: "I want to use the Scanner class that lives in the java.util package".
// A package is like a folder that groups related classes together.
import java.util.Scanner;

public class Example03_ReadingInput {

    public static void main(String[] args) {

        // Create a Scanner that reads from System.in (the keyboard).
        // "new" creates a new object. Think of it as switching on a microphone that listens to the keyboard.
        Scanner scanner = new Scanner(System.in);

        // print (not println) so the cursor stays on the same line as the question.
        System.out.print("What is your name? > ");
        String name = scanner.nextLine();          // reads the WHOLE line the user types

        System.out.print("How old are you? > ");
        int age = scanner.nextInt();               // reads a whole number (int)

        System.out.print("What is your height in metres? > ");
        double height = scanner.nextDouble();      // reads a decimal number (double)

        // Use the values the user gave us.
        System.out.println("Hello, " + name + "!");
        System.out.println("Next year you will be " + (age + 1) + " years old.");
        System.out.println("Your height is " + height + " m.");

        // Close the scanner when we are done - like switching the microphone off.
        scanner.close();
    }
}

// Output (with inputs Priya, 25, 1.62):
// What is your name? > Priya
// How old are you? > 25
// What is your height in metres? > 1.62
// Hello, Priya!
// Next year you will be 26 years old.
// Your height is 1.62 m.
