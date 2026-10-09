/*
 * ============================================================
 *  Topic 2 - Example 01 : Variables
 * ============================================================
 *  What this file demonstrates:
 *    - Declaring a variable, giving it a value, and changing it.
 *    - Declaring and initialising in one line.
 *    - Naming rules and conventions (camelCase).
 *    - A first look at String (text).
 *
 *  How to run:
 *    java Example01_Variables.java
 * ============================================================
 */
public class Example01_Variables {

    public static void main(String[] args) {

        // A VARIABLE is a named box in memory that holds a value.
        // Syntax:   type  name  =  value;

        // 1) Declare first (create an empty box of type int)...
        int studentCount;
        // ...then assign a value later (put something in the box).
        studentCount = 30;
        System.out.println("Students: " + studentCount);

        // 2) Declare AND initialise in one line (most common).
        int booksInLibrary = 1200;
        System.out.println("Books: " + booksInLibrary);

        // 3) Variables can CHANGE (that's why they are called "variables").
        booksInLibrary = booksInLibrary + 50;      // take old value, add 50, store back
        System.out.println("Books after new delivery: " + booksInLibrary);

        // 4) Declaring several variables of the same type in one line (allowed, use sparingly).
        int width = 5, height = 3;
        System.out.println("Area: " + (width * height));

        // 5) Different types for different kinds of data.
        double price = 499.99;          // decimal number
        char grade = 'A';               // ONE character, in SINGLE quotes
        boolean isMember = true;        // true or false only
        String customerName = "Meera";  // text, in DOUBLE quotes (String is a class, not a primitive)

        System.out.println(customerName + " bought an item for " + price
                + ", grade " + grade + ", member? " + isMember);

        // 6) A String has handy built-in abilities (methods). More in Topic 5.
        System.out.println("Name length: " + customerName.length());
        System.out.println("Upper case: " + customerName.toUpperCase());

        // ---------- naming rules ----------
        // ALLOWED:   letters, digits, _ and $ ; must NOT start with a digit ; no spaces ; not a keyword
        int totalMarks = 450;       // good: camelCase, meaningful
        int _count = 1;             // legal but unusual
        int $amount = 2;            // legal but unusual (avoid)
        // int 2ndPlace = 2;        // ERROR: cannot start with a digit
        // int total marks = 450;   // ERROR: no spaces allowed
        // int class = 5;           // ERROR: 'class' is a reserved keyword
        System.out.println("totalMarks=" + totalMarks + ", _count=" + _count + ", $amount=" + $amount);

        // Java is case-sensitive: these are two DIFFERENT variables.
        int score = 10;
        int Score = 20;
        System.out.println("score=" + score + ", Score=" + Score);
    }
}

// Output:
// Students: 30
// Books: 1200
// Books after new delivery: 1250
// Area: 15
// Meera bought an item for 499.99, grade A, member? true
// Name length: 5
// Upper case: MEERA
// totalMarks=450, _count=1, $amount=2
// score=10, Score=20
