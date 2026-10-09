/*
 * ============================================================
 *  Topic 4 - Example 08 : The java.util.Arrays utility class
 * ============================================================
 *  What this file demonstrates the most useful ready-made helpers:
 *    - Arrays.toString      : print an array nicely
 *    - Arrays.sort          : sort ascending (numbers and Strings)
 *    - Arrays.fill          : set every element to one value
 *    - Arrays.copyOf        : copy (and grow / shrink) an array
 *    - Arrays.copyOfRange   : copy a slice
 *    - Arrays.binarySearch  : fast search in a SORTED array
 *    - Arrays.equals        : compare contents of two arrays
 *    - Arrays.deepToString  : print a 2D array
 *
 *  How to run:
 *    java Example08_ArraysUtilityClass.java
 * ============================================================
 */
import java.util.Arrays;

public class Example08_ArraysUtilityClass {

    public static void main(String[] args) {

        // ---------- toString ----------
        int[] scores = {72, 45, 99, 13, 58};
        System.out.println("toString      : " + Arrays.toString(scores));

        // ---------- sort ----------
        Arrays.sort(scores);                       // sorts IN PLACE (changes the array itself)
        System.out.println("sorted        : " + Arrays.toString(scores));

        String[] names = {"Zara", "amit", "Bala", "Chitra"};
        Arrays.sort(names);                        // Strings: dictionary order, CAPITALS before small letters
        System.out.println("sorted names  : " + Arrays.toString(names));

        // ---------- binarySearch (array MUST be sorted first!) ----------
        int position = Arrays.binarySearch(scores, 72);
        System.out.println("binarySearch 72 -> index " + position);
        int missing = Arrays.binarySearch(scores, 60);
        System.out.println("binarySearch 60 -> " + missing + " (negative = not found)");

        // ---------- fill ----------
        int[] seats = new int[6];
        Arrays.fill(seats, -1);                    // e.g. -1 = "seat not booked"
        System.out.println("fill -1       : " + Arrays.toString(seats));

        // ---------- copyOf (also used to "grow" an array) ----------
        int[] bigger = Arrays.copyOf(scores, 7);   // extra slots get default value 0
        System.out.println("copyOf len 7  : " + Arrays.toString(bigger));
        int[] smaller = Arrays.copyOf(scores, 3);  // keeps only the first 3
        System.out.println("copyOf len 3  : " + Arrays.toString(smaller));

        // ---------- copyOfRange (from index inclusive, to index EXCLUSIVE) ----------
        int[] middle = Arrays.copyOfRange(scores, 1, 4);   // indexes 1, 2, 3
        System.out.println("copyOfRange1-4: " + Arrays.toString(middle));

        // ---------- equals vs == ----------
        int[] a = {1, 2, 3};
        int[] b = {1, 2, 3};
        System.out.println("a == b        : " + (a == b));            // false: two different arrays
        System.out.println("Arrays.equals : " + Arrays.equals(a, b));  // true: same contents

        // ---------- deepToString for 2D ----------
        int[][] board = {{1, 0}, {0, 1}};
        System.out.println("deepToString  : " + Arrays.deepToString(board));

        // ---------- bonus: stream-based helpers you'll learn in Topic 14 ----------
        System.out.println("sum via stream: " + Arrays.stream(scores).sum());
        System.out.println("max via stream: " + Arrays.stream(scores).max().getAsInt());
    }
}

// Output:
// toString      : [72, 45, 99, 13, 58]
// sorted        : [13, 45, 58, 72, 99]
// sorted names  : [Bala, Chitra, Zara, amit]
// binarySearch 72 -> index 3
// binarySearch 60 -> -4 (negative = not found)
// fill -1       : [-1, -1, -1, -1, -1, -1]
// copyOf len 7  : [13, 45, 58, 72, 99, 0, 0]
// copyOf len 3  : [13, 45, 58]
// copyOfRange1-4: [45, 58, 72]
// a == b        : false
// Arrays.equals : true
// deepToString  : [[1, 0], [0, 1]]
// sum via stream: 287
// max via stream: 99
