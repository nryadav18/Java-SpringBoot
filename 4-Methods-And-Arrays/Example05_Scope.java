/*
 * ============================================================
 *  Topic 4 - Example 05 : Variable scope
 * ============================================================
 *  What this file demonstrates:
 *    - Scope = the region of code where a variable can be used.
 *    - Class-level (static field): visible in ALL methods of the class.
 *    - Method parameters and local variables: visible only inside their method.
 *    - Block scope: a variable declared inside { } (if, loop) exists only inside that block.
 *    - Loop variables exist only inside the loop.
 *    - Shadowing: a local variable with the same name as a field hides the field.
 *
 *  How to run:
 *    java Example05_Scope.java
 * ============================================================
 */
public class Example05_Scope {

    // CLASS-LEVEL variable (a static field). Every method in this class can see it.
    // Unlike local variables, fields get a default value (0 for int) if not initialised.
    static int totalVisitors = 0;
    static String storeName = "ShopEasy";

    static void registerVisit(String visitorName) {      // visitorName: PARAMETER, lives only in this method
        totalVisitors++;                                  // can use the class-level field
        String message = "Welcome " + visitorName;        // LOCAL variable, lives only in this method
        System.out.println(message + " (visitor #" + totalVisitors + ")");
    }

    static void shadowingDemo() {
        String storeName = "Local Store";                 // SHADOWS the field with the same name
        System.out.println("Local storeName       : " + storeName);
        System.out.println("Field storeName       : " + Example05_Scope.storeName); // ClassName.field reaches the field
    }

    public static void main(String[] args) {
        registerVisit("Ravi");
        registerVisit("Sneha");
        // System.out.println(message);     // ERROR: 'message' only exists inside registerVisit
        // System.out.println(visitorName); // ERROR: parameters are local to their method too

        int discount = 10;                   // local to main, visible from here to the end of main

        if (discount > 5) {
            int bonusPoints = 50;            // BLOCK scope: only inside this if-block
            System.out.println("Discount " + discount + "% and bonus " + bonusPoints + " points");
        }
        // System.out.println(bonusPoints); // ERROR: bonusPoints is gone after the if-block

        for (int i = 1; i <= 3; i++) {       // i exists only inside the loop
            int square = i * i;              // created fresh in every round
            System.out.print(square + " ");
        }
        System.out.println();
        // System.out.println(i);           // ERROR: i is out of scope after the loop

        // Need the value after the loop? Declare it BEFORE the loop.
        int lastSquare = 0;
        for (int i = 1; i <= 3; i++) {
            lastSquare = i * i;
        }
        System.out.println("Last square = " + lastSquare);

        // You can reuse the name i in a NEW loop because the old one is out of scope.
        // But you CANNOT declare a local variable twice in overlapping scopes:
        // int discount = 20;               // ERROR: variable discount is already defined in main

        shadowingDemo();
        System.out.println("Total visitors        : " + totalVisitors);
    }
}

// Output:
// Welcome Ravi (visitor #1)
// Welcome Sneha (visitor #2)
// Discount 10% and bonus 50 points
// 1 4 9
// Last square = 9
// Local storeName       : Local Store
// Field storeName       : ShopEasy
// Total visitors        : 2
