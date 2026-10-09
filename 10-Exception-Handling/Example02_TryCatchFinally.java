/*
 * Example02_TryCatchFinally.java
 *
 * What this file demonstrates:
 *   - try / catch / finally and the ORDER in which the lines run
 *   - several catch blocks: the FIRST matching one wins, so put specific ones first
 *   - multi-catch:  catch (TypeA | TypeB e)
 *   - finally ALWAYS runs: after success, after a caught exception, even after "return"
 *
 * Domain: an ATM withdrawal and a simple calculator.
 *
 * How to run:
 *   java Example02_TryCatchFinally.java
 *
 * Output:
 *   === Case A: everything OK ===
 *   try: start
 *   try: dispensing Rs.500
 *   finally: card returned
 *   after try/catch/finally
 *   === Case B: an exception happens ===
 *   try: start
 *   catch: Not enough cash in ATM
 *   finally: card returned
 *   after try/catch/finally
 *   === Multiple catch blocks ===
 *   "10/2" -> 5
 *   "10/0" -> Cannot divide by zero
 *   "ten/2" -> Not a number: ten
 *   "10" -> Bad format, expected a/b
 *   === Multi-catch ===
 *   Input problem (NumberFormatException)
 *   Input problem (ArrayIndexOutOfBoundsException)
 *   === finally runs even after return ===
 *   finally: closing the shop
 *   Returned: OPEN
 */
public class Example02_TryCatchFinally {

    public static void main(String[] args) {
        System.out.println("=== Case A: everything OK ===");
        withdraw(500, 2000);

        System.out.println("=== Case B: an exception happens ===");
        withdraw(5000, 2000);

        System.out.println("=== Multiple catch blocks ===");
        String[] inputs = {"10/2", "10/0", "ten/2", "10"};
        for (String input : inputs) {
            System.out.println("\"" + input + "\" -> " + calculate(input));
        }

        System.out.println("=== Multi-catch ===");
        parseBoth("12x");     // NumberFormatException
        parseBoth("");        // split("") gives [""] -> parts[1] does not exist

        System.out.println("=== finally runs even after return ===");
        System.out.println("Returned: " + shopStatus());
    }

    static void withdraw(int amount, int cashInAtm) {
        try {
            System.out.println("try: start");
            if (amount > cashInAtm) {
                // We create and throw an exception ourselves (more on "throw" in Example05)
                throw new IllegalStateException("Not enough cash in ATM");
            }
            System.out.println("try: dispensing Rs." + amount);   // skipped if the exception happened
        } catch (IllegalStateException e) {
            System.out.println("catch: " + e.getMessage());
        } finally {
            // Clean-up code that MUST run no matter what. The ATM must always return your card!
            System.out.println("finally: card returned");
        }
        System.out.println("after try/catch/finally");
    }

    static String calculate(String expression) {
        try {
            String[] parts = expression.split("/");
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            return String.valueOf(a / b);
        } catch (ArithmeticException e) {                 // most specific problems first...
            return "Cannot divide by zero";
        } catch (NumberFormatException e) {
            return "Not a number: " + e.getMessage().replace("For input string: ", "").replace("\"", "");
        } catch (RuntimeException e) {                    // ...the general "catch the rest" LAST
            return "Bad format, expected a/b";
        }
        // If RuntimeException were FIRST, the compiler would refuse:
        //   "exception ArithmeticException has already been caught"
        // because the general block would swallow everything and the others could never run.
    }

    static void parseBoth(String text) {
        try {
            String[] parts = text.split(",");
            int first = Integer.parseInt(parts[0].isEmpty() ? "0" : parts[0]);
            int second = Integer.parseInt(parts[1]);
            System.out.println(first + second);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // multi-catch: ONE block for SEVERAL unrelated exception types handled the same way.
            // (The types must not be parent/child of each other.)
            System.out.println("Input problem (" + e.getClass().getSimpleName() + ")");
        }
    }

    static String shopStatus() {
        try {
            return "OPEN";                       // the return value is remembered...
        } finally {
            System.out.println("finally: closing the shop");   // ...finally runs BEFORE the method really returns
        }
        // Never put "return" inside finally: it would silently replace the try's return value
        // AND swallow any exception. (Only System.exit() or a JVM crash can skip finally.)
    }
}
