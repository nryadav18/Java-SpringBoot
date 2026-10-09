/*
 * ============================================================
 *  Topic 4 - Example 01 : Method basics
 * ============================================================
 *  What this file demonstrates:
 *    - Why methods: write once, reuse many times, give a name to a task.
 *    - Method syntax: modifiers, return type, name, parameters, body.
 *    - void methods (do something) vs methods that return a value (calculate something).
 *    - Parameters (in the definition) vs arguments (in the call).
 *    - Calling a method and using its result.
 *    - Early return (guard clauses) to avoid deep nesting.
 *
 *  How to run:
 *    java Example01_MethodBasics.java
 * ============================================================
 */
public class Example01_MethodBasics {

    // ---------------------------------------------------------
    // Anatomy of a method:
    //
    //   static   double   calculateGst ( double amount )   { ...body... }
    //   ^        ^        ^               ^
    //   modifier return    name            parameter list (type + name)
    //            type
    //
    // 'static' lets us call it directly from main without creating an object (Topic 6 & 9 explain more).
    // ---------------------------------------------------------

    // 1) A void method: it DOES something (prints) but gives nothing back.
    static void printWelcomeBanner() {
        System.out.println("*******************************");
        System.out.println("*   Welcome to ShopEasy Store  *");
        System.out.println("*******************************");
    }

    // 2) A method with a parameter: 'name' is a PARAMETER (a placeholder variable).
    static void greetCustomer(String name) {
        System.out.println("Hello, " + name + "! Happy shopping.");
    }

    // 3) A method that RETURNS a value. The return type (double) says what comes back.
    static double calculateGst(double amount) {
        double gst = amount * 0.18;
        return gst;                 // 'return' sends the value back to the caller and ends the method
    }

    // 4) Several parameters, separated by commas.
    static double lineTotal(double unitPrice, int quantity) {
        return unitPrice * quantity;   // you can return an expression directly
    }

    // 5) Returning a boolean - name it like a question: isXxx / hasXxx / canXxx.
    static boolean isEligibleForFreeDelivery(double cartTotal) {
        return cartTotal >= 500;
    }

    // 6) Methods can call other methods.
    static double finalAmount(double unitPrice, int quantity) {
        double subtotal = lineTotal(unitPrice, quantity);
        return subtotal + calculateGst(subtotal);
    }

    // 7) Early return / guard clauses: handle bad input first, then do the real work.
    //    Compare with deeply nested if/else from Topic 3 - this is much flatter and easier to read.
    static String withdraw(double balance, double amount) {
        if (amount <= 0) {
            return "Rejected: amount must be positive";
        }
        if (amount > balance) {
            return "Rejected: insufficient balance";
        }
        return "Approved: new balance " + (balance - amount);
    }

    public static void main(String[] args) {
        printWelcomeBanner();                 // call a void method: just a statement

        greetCustomer("Ravi");                // "Ravi" is an ARGUMENT (the actual value)
        greetCustomer("Sneha");               // reuse! same code, different data

        double gst = calculateGst(1000);      // store the returned value
        System.out.println("GST on 1000 = " + gst);

        // Use the returned value directly inside an expression:
        System.out.println("Pen x 4 = " + lineTotal(12.5, 4));

        double cart = lineTotal(150, 3);
        if (isEligibleForFreeDelivery(cart)) {
            System.out.println("Cart " + cart + ": free delivery!");
        } else {
            System.out.println("Cart " + cart + ": add " + (500 - cart) + " more for free delivery");
        }

        System.out.println("Final amount for 2 x 250 = " + finalAmount(250, 2));

        System.out.println(withdraw(1000, -5));
        System.out.println(withdraw(1000, 5000));
        System.out.println(withdraw(1000, 300));

        // Calling a method that returns a value WITHOUT using the value is allowed (value is ignored):
        calculateGst(500);
    }
}

// Output:
// *******************************
// *   Welcome to ShopEasy Store  *
// *******************************
// Hello, Ravi! Happy shopping.
// Hello, Sneha! Happy shopping.
// GST on 1000 = 180.0
// Pen x 4 = 50.0
// Cart 450.0: add 50.0 more for free delivery
// Final amount for 2 x 250 = 590.0
// Rejected: amount must be positive
// Rejected: insufficient balance
// Approved: new balance 700.0
