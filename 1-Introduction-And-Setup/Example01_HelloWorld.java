/*
 * ============================================================
 *  Topic 1 - Example 01 : Hello World
 * ============================================================
 *  What this file demonstrates:
 *    - The smallest complete Java program.
 *    - A class, the main method, and printing text to the screen.
 *
 *  How to run (open a terminal inside this folder):
 *    Option A (quick, Java 11+):   java Example01_HelloWorld.java
 *    Option B (classic 2 steps):   javac -d out Example01_HelloWorld.java
 *                                  java -cp out Example01_HelloWorld
 *
 *  Expected output:
 *    Hello, World!
 *    Welcome to the Java + Spring Boot course.
 * ============================================================
 */

// "public class Example01_HelloWorld" creates a CLASS.
// A class is a container for code. In Java, ALL code must live inside a class.
// Rule: the public class name must match the file name exactly
//       (Example01_HelloWorld.java  ->  class Example01_HelloWorld).
public class Example01_HelloWorld {

    // This is the MAIN METHOD - the "front door" of every Java program.
    // When you run the program, the JVM looks for exactly this line and starts here.
    //   public  -> anyone (including the JVM) is allowed to call it
    //   static  -> it can run without creating an object first
    //   void    -> it does not give back (return) any value
    //   main    -> the special name the JVM searches for
    //   String[] args -> extra words you can pass from the command line (we ignore them for now)
    public static void main(String[] args) {

        // System.out.println(...) prints text and then moves to a new line.
        //   System -> a built-in class that represents "the computer system"
        //   out    -> the standard output (your terminal / console)
        //   println-> "print line"
        // Text inside double quotes "..." is called a String.
        // Every statement ends with a semicolon ;
        System.out.println("Hello, World!");
        System.out.println("Welcome to the Java + Spring Boot course.");
    }
}

// Output:
// Hello, World!
// Welcome to the Java + Spring Boot course.
