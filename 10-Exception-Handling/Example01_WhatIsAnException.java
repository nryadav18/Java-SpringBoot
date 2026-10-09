/*
 * Example01_WhatIsAnException.java
 *
 * What this file demonstrates:
 *   - What an exception IS: an object that Java "throws" when something goes wrong at runtime.
 *   - 4 very common exceptions you WILL meet:
 *       ArithmeticException            (divide an int by zero)
 *       ArrayIndexOutOfBoundsException (index outside the array)
 *       NullPointerException           (calling a method on null)
 *       NumberFormatException          ("abc" is not a number)
 *   - Catching them so the program does NOT crash.
 *   - Reading a STACK TRACE (printed with e.printStackTrace()).
 *
 * How to run:
 *   java Example01_WhatIsAnException.java
 *
 * Output (stack-trace lines go to the error stream, so VS Code may show them in red,
 * and their position relative to normal lines can vary slightly):
 *   Program started
 *   1) Caught: java.lang.ArithmeticException: / by zero
 *   2) Caught: java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 3
 *   3) Caught: java.lang.NullPointerException -> customer name was null
 *   4) Caught: java.lang.NumberFormatException: For input string: "12a"
 *   5) Exception object details:
 *      getMessage()  = / by zero
 *      class name    = java.lang.ArithmeticException
 *   6) Full stack trace below (read it from the TOP):
 *   java.lang.ArithmeticException: / by zero
 *   	at Example01_WhatIsAnException.splitBill(Example01_WhatIsAnException.java:97)
 *   	at Example01_WhatIsAnException.calculateShare(Example01_WhatIsAnException.java:102)
 *   	at Example01_WhatIsAnException.main(Example01_WhatIsAnException.java:79)
 *   	at java.base/jdk.internal.reflect...   (a few more lines from the JDK's own
 *   	at jdk.compiler/com.sun.tools...        "java File.java" launcher - ignore them)
 *   Program finished normally - no crash!
 */
public class Example01_WhatIsAnException {

    public static void main(String[] args) {
        System.out.println("Program started");

        // 1) ArithmeticException: integer division by zero is impossible
        try {
            int pizzas = 10;
            int friends = 0;
            int each = pizzas / friends;       // BOOM - Java throws an ArithmeticException object here
            System.out.println("Each friend gets " + each);   // this line is SKIPPED
        } catch (ArithmeticException e) {
            System.out.println("1) Caught: " + e);   // printing e shows "ClassName: message"
        }

        // 2) ArrayIndexOutOfBoundsException: valid indexes of a 3-element array are 0, 1, 2
        try {
            int[] marks = {80, 75, 92};
            System.out.println(marks[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("2) Caught: " + e);
        }

        // 3) NullPointerException: the variable points to NOTHING, so there is no object to ask
        try {
            String customerName = null;
            System.out.println(customerName.length());
        } catch (NullPointerException e) {
            // Java 14+ has "helpful NPE messages" (which variable was null). We print our own text
            // here to keep the output short.
            System.out.println("3) Caught: " + e.getClass().getName() + " -> customer name was null");
        }

        // 4) NumberFormatException: user typed letters where we expected a number
        try {
            int quantity = Integer.parseInt("12a");
            System.out.println("Quantity: " + quantity);
        } catch (NumberFormatException e) {
            System.out.println("4) Caught: " + e);
        }

        // 5) An exception is a normal Java OBJECT with useful methods
        try {
            calculateShare(500, 0);
        } catch (ArithmeticException e) {
            System.out.println("5) Exception object details:");
            System.out.println("   getMessage()  = " + e.getMessage());
            System.out.println("   class name    = " + e.getClass().getName());
            System.out.println("6) Full stack trace below (read it from the TOP):");
            e.printStackTrace();   // prints to System.err (the error stream)
            System.err.flush();
        }

        System.out.println("Program finished normally - no crash!");

        // Try this: remove a try/catch above (keep the risky line). The program will CRASH there
        // with "Exception in thread "main" ..." and the remaining lines will never run.
    }

    // The exception is thrown here...
    static int splitBill(int amount, int people) {
        return amount / people;
    }

    // ...travels up through this method (which does not catch it)...
    static int calculateShare(int amount, int people) {
        return splitBill(amount, people);
    }
    // ...and is finally caught in main(). The stack trace shows this whole path.
}
