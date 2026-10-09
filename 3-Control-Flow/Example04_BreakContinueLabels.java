/*
 * ============================================================
 *  Topic 3 - Example 04 : break, continue, labelled break/continue
 * ============================================================
 *  What this file demonstrates:
 *    - break: leave the loop immediately.
 *    - continue: skip the rest of THIS round and go to the next round.
 *    - Labelled break: leave an OUTER loop from inside an inner loop.
 *    - Labelled continue: jump to the next round of the OUTER loop.
 *
 *  How to run:
 *    java Example04_BreakContinueLabels.java
 * ============================================================
 */
public class Example04_BreakContinueLabels {

    public static void main(String[] args) {

        // ---------- 1. break ----------
        // Search for the first order above 1000 and stop as soon as we find it.
        int[] orderAmounts = {250, 780, 1500, 300, 2200};
        for (int i = 0; i < orderAmounts.length; i++) {
            System.out.println("Checking order #" + i + " = " + orderAmounts[i]);
            if (orderAmounts[i] > 1000) {
                System.out.println("Found first big order at index " + i);
                break;      // no need to check the rest
            }
        }

        // ---------- 2. continue ----------
        // Print only the odd numbers 1..9: skip the even ones.
        System.out.print("Odd numbers: ");
        for (int i = 1; i <= 9; i++) {
            if (i % 2 == 0) {
                continue;   // skip printing, jump to i++
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // continue with real data: skip out-of-stock items
        String[] products = {"Laptop", "Mouse", "Keyboard", "Monitor"};
        int[] stock = {5, 0, 12, 0};
        for (int i = 0; i < products.length; i++) {
            if (stock[i] == 0) {
                continue;
            }
            System.out.println(products[i] + " is available (" + stock[i] + ")");
        }

        // ---------- 3. Labelled break ----------
        // A normal break only exits the INNER loop. To exit BOTH loops,
        // put a label (any name followed by :) before the outer loop and use "break label;".
        // Example: find a seat in a cinema (rows x seats) that is free.
        boolean[][] seatTaken = {
            {true, true, true},
            {true, false, true},   // row 1, seat 1 is free
            {false, false, false}
        };
        int foundRow = -1, foundSeat = -1;
        search:                                   // <-- the label
        for (int row = 0; row < seatTaken.length; row++) {
            for (int seat = 0; seat < seatTaken[row].length; seat++) {
                if (!seatTaken[row][seat]) {
                    foundRow = row;
                    foundSeat = seat;
                    break search;                 // exits BOTH loops
                }
            }
        }
        System.out.println("First free seat: row " + foundRow + ", seat " + foundSeat);

        // ---------- 4. Labelled continue ----------
        // For each student, print "passed" only if no subject mark is below 40.
        int[][] marks = {
            {78, 65, 90},
            {55, 32, 70},      // failed one subject
            {88, 91, 79}
        };
        nextStudent:
        for (int s = 0; s < marks.length; s++) {
            for (int m : marks[s]) {
                if (m < 40) {
                    System.out.println("Student " + s + " failed a subject");
                    continue nextStudent;          // skip to the next student
                }
            }
            System.out.println("Student " + s + " passed all subjects");
        }
    }
}

// Output:
// Checking order #0 = 250
// Checking order #1 = 780
// Checking order #2 = 1500
// Found first big order at index 2
// Odd numbers: 1 3 5 7 9
// Laptop is available (5)
// Keyboard is available (12)
// First free seat: row 1, seat 1
// Student 0 passed all subjects
// Student 1 failed a subject
// Student 2 passed all subjects
