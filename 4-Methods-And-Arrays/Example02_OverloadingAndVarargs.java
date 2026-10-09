/*
 * ============================================================
 *  Topic 4 - Example 02 : Method overloading and varargs
 * ============================================================
 *  What this file demonstrates:
 *    - Overloading: several methods with the SAME name but DIFFERENT parameter lists.
 *    - How Java picks which overload to call (by number and types of arguments).
 *    - Return type alone does NOT make a different overload.
 *    - Varargs (int... numbers): a method that accepts any number of arguments.
 *
 *  How to run:
 *    java Example02_OverloadingAndVarargs.java
 * ============================================================
 */
public class Example02_OverloadingAndVarargs {

    // ---------- Overloading ----------
    // Same name "area", different parameters. Java chooses based on the arguments you pass.

    // Area of a square
    static double area(double side) {
        return side * side;
    }

    // Area of a rectangle (2 parameters)
    static double area(double length, double width) {
        return length * width;
    }

    // Area of a circle - same NUMBER of parameters as square, but we need a different signature,
    // so here we use a different TYPE (int radius) just to demonstrate type-based selection.
    static double area(int radius) {
        return Math.PI * radius * radius;
    }

    // NOT allowed: same parameter list, only the return type differs.
    // static int area(double side) { ... }   // ERROR: method area(double) is already defined

    // Overloads are great for "the same action with different inputs":
    static void printReceipt(String item, double price) {
        System.out.printf("%s: %.2f%n", item, price);
    }

    static void printReceipt(String item, double price, int quantity) {
        System.out.printf("%s x %d: %.2f%n", item, quantity, price * quantity);
    }

    // ---------- Varargs ----------
    // "int... numbers" means: zero or more ints. Inside the method, numbers is an ARRAY (int[]).
    static int sum(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    // Varargs must be the LAST parameter, and there can be only one per method.
    static void printTags(String productName, String... tags) {
        System.out.print(productName + " tags(" + tags.length + "):");
        for (String tag : tags) {
            System.out.print(" #" + tag);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("Square 4      = " + area(4.0));        // area(double)
        System.out.println("Rectangle 4x5 = " + area(4.0, 5.0));   // area(double, double)
        System.out.printf("Circle r=2    = %.2f%n", area(2));      // area(int) - exact match wins

        printReceipt("Notebook", 60.0);
        printReceipt("Notebook", 60.0, 3);

        System.out.println("sum()            = " + sum());              // zero arguments
        System.out.println("sum(5)           = " + sum(5));
        System.out.println("sum(1, 2, 3, 4)  = " + sum(1, 2, 3, 4));
        int[] scores = {10, 20, 30};
        System.out.println("sum(array)       = " + sum(scores));        // you can also pass an array

        printTags("Laptop", "electronics", "sale");
        printTags("Book");                                              // no tags at all

        // You already know a famous varargs method: printf(String format, Object... args)
        System.out.printf("%s has %d items%n", "Cart", 3);
    }
}

// Output:
// Square 4      = 16.0
// Rectangle 4x5 = 20.0
// Circle r=2    = 12.57
// Notebook: 60.00
// Notebook x 3: 180.00
// sum()            = 0
// sum(5)           = 5
// sum(1, 2, 3, 4)  = 10
// sum(array)       = 60
// Laptop tags(2): #electronics #sale
// Book tags(0):
// Cart has 3 items
