/*
 * Example03_CheckedVsUnchecked.java
 *
 * What this file demonstrates:
 *   - CHECKED exceptions (extend Exception, but not RuntimeException):
 *       the COMPILER forces you to handle them (catch) or declare them (throws).
 *       They are for problems OUTSIDE your control: missing file, network down, ...
 *       Example: java.io.IOException, java.nio.file.NoSuchFileException, InterruptedException
 *   - UNCHECKED exceptions (extend RuntimeException):
 *       the compiler does NOT force you. They usually mean a BUG in the code
 *       (null, bad index, bad argument).
 *   - Errors (extend Error): serious JVM problems such as StackOverflowError. Don't catch them normally.
 *
 * How to run:
 *   java Example03_CheckedVsUnchecked.java
 *
 * Output:
 *   --- Checked exception ---
 *   Could not read file: NoSuchFileException -> missing-orders.txt
 *   --- Checked exception declared with throws ---
 *   Waiting 100 ms for the courier...
 *   Courier arrived
 *   --- Unchecked exception ---
 *   Bug found: IllegalArgumentException -> Quantity must be positive: -2
 *   --- Error (just to SEE one; never do this in real code) ---
 *   Error caught: StackOverflowError (a method called itself forever)
 *   --- Is it checked? ---
 *   IOException                    checked
 *   InterruptedException           checked
 *   NullPointerException           unchecked
 *   IllegalArgumentException       unchecked
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Example03_CheckedVsUnchecked {

    public static void main(String[] args) {
        System.out.println("--- Checked exception ---");
        // Files.readString(...) is declared as "throws IOException".
        // If you remove this try/catch, the code will NOT COMPILE:
        //   error: unreported exception IOException; must be caught or declared to be thrown
        try {
            String text = Files.readString(Path.of("missing-orders.txt"));
            System.out.println(text);
        } catch (IOException e) {
            System.out.println("Could not read file: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }

        System.out.println("--- Checked exception declared with throws ---");
        try {
            waitForCourier();
        } catch (InterruptedException e) {
            System.out.println("Waiting was interrupted");
        }

        System.out.println("--- Unchecked exception ---");
        // No "try" is REQUIRED by the compiler here. We add one only to show the message.
        try {
            addToCart("Pen", -2);
        } catch (IllegalArgumentException e) {
            System.out.println("Bug found: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }

        System.out.println("--- Error (just to SEE one; never do this in real code) ---");
        try {
            callForever(1);
        } catch (StackOverflowError e) {
            System.out.println("Error caught: StackOverflowError (a method called itself forever)");
        }

        System.out.println("--- Is it checked? ---");
        printKind(new IOException());
        printKind(new InterruptedException());
        printKind(new NullPointerException());
        printKind(new IllegalArgumentException());
    }

    // "throws InterruptedException" = "I don't handle this; whoever calls me must deal with it."
    static void waitForCourier() throws InterruptedException {
        System.out.println("Waiting 100 ms for the courier...");
        Thread.sleep(100);              // Thread.sleep declares a CHECKED InterruptedException
        System.out.println("Courier arrived");
    }

    // Unchecked: no "throws" needed (you MAY still write it, as documentation).
    static void addToCart(String item, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
        System.out.println("Added " + quantity + " x " + item);
    }

    static int callForever(int n) {
        return callForever(n + 1);      // no stop condition -> the call stack overflows
    }

    static void printKind(Exception e) {
        // Checked = it is an Exception but NOT a RuntimeException.
        String kind = (e instanceof RuntimeException) ? "unchecked" : "checked";
        System.out.printf("%-30s %s%n", e.getClass().getSimpleName(), kind);
    }
}
