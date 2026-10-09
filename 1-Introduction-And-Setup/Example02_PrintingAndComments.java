/*
 * ============================================================
 *  Topic 1 - Example 02 : Printing and Comments
 * ============================================================
 *  What this file demonstrates:
 *    - The difference between print and println.
 *    - Escape sequences: \n (new line), \t (tab), \" (quote), \\ (backslash).
 *    - Joining text and numbers with +.
 *    - printf for formatted output.
 *    - The three kinds of comments in Java.
 *
 *  How to run:
 *    java Example02_PrintingAndComments.java
 * ============================================================
 */

/**
 * This is a JAVADOC comment (starts with slash-star-star).
 * Tools can turn these comments into HTML documentation.
 * We use them to describe classes and methods for other developers.
 */
public class Example02_PrintingAndComments {

    public static void main(String[] args) {

        // 1) SINGLE-LINE comment: everything after // on this line is ignored by Java.

        /*
           2) MULTI-LINE comment: everything between slash-star and star-slash is ignored.
              Useful for longer explanations.
        */

        // ---------- print vs println ----------
        // print   -> prints and STAYS on the same line
        // println -> prints and then MOVES to the next line
        System.out.print("Java ");
        System.out.print("is ");
        System.out.println("fun!");          // the line ends here
        System.out.println("This is a new line.");

        // An empty println() just prints a blank line - handy for spacing.
        System.out.println();

        // ---------- escape sequences ----------
        // Some characters are special inside a String, so we "escape" them with a backslash.
        System.out.println("Line one\nLine two");            // \n = new line
        System.out.println("Name:\tRavi");                    // \t = tab (a wide space)
        System.out.println("She said \"Hello\"");             // \" = a double quote inside text
        System.out.println("Windows path: C:\\Java\\bin");    // \\ = one backslash

        System.out.println();

        // ---------- joining (concatenating) with + ----------
        // When + is used with a String, it GLUES things together.
        System.out.println("Course: " + "Java" + " + " + "Spring Boot");
        System.out.println("Topics in this course: " + 71);

        // Careful! Java reads left to right.
        System.out.println("Sum: " + 2 + 3);    // "Sum: 2" then + 3 -> "Sum: 23"  (text glue!)
        System.out.println("Sum: " + (2 + 3));  // brackets first: 2 + 3 = 5 -> "Sum: 5"
        System.out.println(2 + 3 + " is the sum"); // 2 + 3 = 5 first (both numbers) -> "5 is the sum"

        System.out.println();

        // ---------- printf: formatted printing ----------
        // %s = put a String here, %d = put a whole number here, %.2f = decimal with 2 digits
        // %n = new line (works on every operating system)
        System.out.printf("Student: %s, Age: %d, Fee: %.2f%n", "Asha", 21, 1500.5);
    }
}

// Output:
// Java is fun!
// This is a new line.
//
// Line one
// Line two
// Name:	Ravi
// She said "Hello"
// Windows path: C:\Java\bin
//
// Course: Java + Spring Boot
// Topics in this course: 71
// Sum: 23
// Sum: 5
// 5 is the sum
//
// Student: Asha, Age: 21, Fee: 1500.50
