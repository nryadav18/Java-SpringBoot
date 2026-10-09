/*
 * ============================================================
 *  Topic 4 - Example 06 : Array basics
 * ============================================================
 *  What this file demonstrates:
 *    - What an array is: a fixed-size row of boxes of the SAME type, numbered from 0.
 *    - Three ways to create an array (declare + new, initializer list, new with values).
 *    - Reading and writing elements with [index].
 *    - .length (a field, no brackets!).
 *    - Iterating with a classic for loop and with for-each.
 *    - Default values (0, 0.0, false, null).
 *    - Arrays are reference types: assigning copies the REFERENCE, not the elements.
 *    - What happens with an invalid index (ArrayIndexOutOfBoundsException).
 *
 *  How to run:
 *    java Example06_ArrayBasics.java
 * ============================================================
 */
import java.util.Arrays;

public class Example06_ArrayBasics {

    public static void main(String[] args) {

        // ---------- 1. Creating arrays ----------
        // Way A: declare + create with a size. All elements start with default values.
        int[] marks = new int[5];          // 5 ints, all 0. Indexes 0,1,2,3,4
        marks[0] = 78;                     // write into box 0
        marks[1] = 91;
        marks[4] = 66;                     // boxes 2 and 3 stay 0
        System.out.println("marks = " + Arrays.toString(marks));

        // Way B: initializer list - size is worked out from the values.
        String[] cities = {"Delhi", "Mumbai", "Chennai", "Kolkata"};

        // Way C: new + values (needed when you create an array outside a declaration, e.g. as an argument).
        double[] prices;
        prices = new double[] {99.5, 250.0, 15.75};

        // The other bracket style works too, but the first is preferred in Java:
        int scores[] = {1, 2, 3};          // legal, C-style - avoid
        System.out.println("C-style array length: " + scores.length);

        // ---------- 2. Reading elements and length ----------
        System.out.println("First city: " + cities[0]);
        System.out.println("Last city : " + cities[cities.length - 1]);   // last index = length - 1
        System.out.println("Number of cities: " + cities.length);          // length is a FIELD: no ()

        // ---------- 3. Iterating ----------
        // Classic for: you get the INDEX (needed to change elements or print positions).
        for (int i = 0; i < prices.length; i++) {
            System.out.println("Item " + (i + 1) + " costs " + prices[i]);
        }
        // for-each: simplest when you just need each VALUE.
        double total = 0;
        for (double price : prices) {
            total += price;
        }
        System.out.println("Total price: " + total);

        // ---------- 4. Default values ----------
        int[] ints = new int[2];
        double[] doubles = new double[2];
        boolean[] flags = new boolean[2];
        char[] chars = new char[2];
        String[] names = new String[2];    // objects default to null ("no object yet")
        System.out.println("int defaults    : " + Arrays.toString(ints));
        System.out.println("double defaults : " + Arrays.toString(doubles));
        System.out.println("boolean defaults: " + Arrays.toString(flags));
        System.out.println("char default is the \\u0000 character, code " + (int) chars[0]);
        System.out.println("String defaults : " + Arrays.toString(names));

        // ---------- 5. Arrays are references ----------
        int[] original = {1, 2, 3};
        int[] alias = original;            // copies the REFERENCE: both names point to ONE array
        alias[0] = 100;
        System.out.println("original after changing alias: " + Arrays.toString(original));

        int[] realCopy = Arrays.copyOf(original, original.length);  // a NEW array with the same values
        realCopy[1] = 200;
        System.out.println("original after changing copy : " + Arrays.toString(original));
        System.out.println("copy                         : " + Arrays.toString(realCopy));

        // Printing an array directly shows a type code + hash, not the values:
        // System.out.println(original);  -> something like [I@1b6d3586
        // Use Arrays.toString(original) instead.

        // ---------- 6. Invalid index ----------
        // Arrays have a FIXED size. Index must be 0 .. length-1.
        // cities[4] would crash with:
        //   Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException:
        //   Index 4 out of bounds for length 4
        int index = 4;
        if (index >= 0 && index < cities.length) {
            System.out.println(cities[index]);
        } else {
            System.out.println("Index " + index + " is out of bounds for length " + cities.length);
        }
    }
}

// Output:
// marks = [78, 91, 0, 0, 66]
// C-style array length: 3
// First city: Delhi
// Last city : Kolkata
// Number of cities: 4
// Item 1 costs 99.5
// Item 2 costs 250.0
// Item 3 costs 15.75
// Total price: 365.25
// int defaults    : [0, 0]
// double defaults : [0.0, 0.0]
// boolean defaults: [false, false]
// char default is the \u0000 character, code 0
// String defaults : [null, null]
// original after changing alias: [100, 2, 3]
// original after changing copy : [100, 2, 3]
// copy                         : [100, 200, 3]
// Index 4 is out of bounds for length 4
