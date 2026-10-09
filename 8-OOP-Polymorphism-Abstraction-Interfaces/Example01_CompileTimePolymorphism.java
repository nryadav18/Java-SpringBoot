/*
 * Example01_CompileTimePolymorphism.java
 * ------------------------------------------------------------
 * What this file shows:
 *   COMPILE-TIME polymorphism (also called static polymorphism / method overloading):
 *   ONE method name, MANY parameter lists. The COMPILER picks the right version
 *   by looking at the argument types - before the program even runs.
 *
 *   1. Overloading by number of parameters.
 *   2. Overloading by type of parameters.
 *   3. Overloading by order of parameters.
 *   4. Automatic type promotion (an int argument can use a long/double version).
 *   5. Changing ONLY the return type is NOT overloading (does not compile).
 *
 * How to run (from this folder):
 *   java Example01_CompileTimePolymorphism.java
 */
public class Example01_CompileTimePolymorphism {

    public static void main(String[] args) {
        PriceCalculator calc = new PriceCalculator();

        // The compiler chooses the method by looking at the arguments:
        System.out.println("total(100, 2)            = " + calc.total(100, 2));              // int, int
        System.out.println("total(99.5, 3)           = " + calc.total(99.5, 3));             // double, int
        System.out.println("total(100, 2, 10)        = " + calc.total(100, 2, 10));          // 3 ints
        System.out.println("total(\"Pen\", 3)          = " + calc.total("Pen", 3));          // String, int
        System.out.println("total(3, \"Pen\")          = " + calc.total(3, "Pen"));          // int, String (order!)

        // Type promotion: there is no total(short, short), so the short values are
        // automatically widened to int and total(int, int) is used.
        short a = 50, b = 2;
        System.out.println("total(short, short)      = " + calc.total(a, b));

        // You already use overloading all the time: println has a version for every type!
        System.out.println(42);        // println(int)
        System.out.println(4.2);       // println(double)
        System.out.println('x');       // println(char)
        System.out.println(true);      // println(boolean)
    }
}

class PriceCalculator {

    int total(int unitPrice, int quantity) {
        return unitPrice * quantity;
    }

    double total(double unitPrice, int quantity) {          // different TYPE
        return unitPrice * quantity;
    }

    int total(int unitPrice, int quantity, int discountPercent) {   // different NUMBER
        int gross = unitPrice * quantity;
        return gross - gross * discountPercent / 100;
    }

    String total(String product, int quantity) {
        return quantity + " x " + product;
    }

    String total(int quantity, String product) {           // different ORDER
        return product + " (qty " + quantity + ")";
    }

    // long total(int unitPrice, int quantity) { ... }
    // ^ does NOT compile: "method total(int,int) is already defined".
    //   Only the return type differs, so the compiler couldn't choose between them.
}

/*
Output:
total(100, 2)            = 200
total(99.5, 3)           = 298.5
total(100, 2, 10)        = 180
total("Pen", 3)          = 3 x Pen
total(3, "Pen")          = Pen (qty 3)
total(short, short)      = 100
42
4.2
x
true
*/
