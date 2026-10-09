/*
 * ============================================================
 *  Topic 4 - Example 07 : Two-dimensional (2D) arrays
 * ============================================================
 *  What this file demonstrates:
 *    - A 2D array is an "array of arrays" - think of a table with rows and columns.
 *    - Creating with new int[rows][cols] and with nested initializer lists.
 *    - Accessing grid[row][col], rows = grid.length, columns of row r = grid[r].length.
 *    - Nested loops to walk through every cell (row totals, column totals).
 *    - Jagged arrays (rows of different lengths).
 *    - Arrays.deepToString to print a 2D array.
 *
 *  How to run:
 *    java Example07_TwoDimensionalArrays.java
 * ============================================================
 */
import java.util.Arrays;

public class Example07_TwoDimensionalArrays {

    public static void main(String[] args) {

        // ---------- 1. Marks of 3 students in 4 subjects ----------
        //                 Maths Physics Chem English
        int[][] marks = {
                {78,   85,   62,   90},     // student 0 (row 0)
                {92,   88,   79,   84},     // student 1
                {55,   67,   71,   60}      // student 2
        };
        System.out.println("Rows (students)   : " + marks.length);
        System.out.println("Cols (subjects)   : " + marks[0].length);
        System.out.println("Student 1, Chem   : " + marks[1][2]);   // row 1, column 2 -> 79
        System.out.println("Whole table       : " + Arrays.deepToString(marks));

        // ---------- 2. Row totals (one total per student) ----------
        for (int row = 0; row < marks.length; row++) {
            int total = 0;
            for (int col = 0; col < marks[row].length; col++) {
                total += marks[row][col];
            }
            System.out.println("Student " + row + " total = " + total);
        }

        // ---------- 3. Column averages (one average per subject) ----------
        String[] subjects = {"Maths", "Physics", "Chem", "English"};
        for (int col = 0; col < subjects.length; col++) {
            int sum = 0;
            for (int row = 0; row < marks.length; row++) {
                sum += marks[row][col];
            }
            System.out.printf("%-8s average = %.2f%n", subjects[col], (double) sum / marks.length);
        }

        // ---------- 4. Creating with a size, then filling (multiplication grid) ----------
        int[][] grid = new int[3][3];          // 3 rows x 3 cols, all 0
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                grid[r][c] = (r + 1) * (c + 1);
            }
        }
        System.out.println("3x3 multiplication grid:");
        for (int[] rowArray : grid) {          // for-each over rows: each row IS an int[]
            for (int value : rowArray) {
                System.out.printf("%3d", value);
            }
            System.out.println();
        }

        // ---------- 5. Jagged array: rows can have different lengths ----------
        // e.g. number of orders each delivery agent handled on each day they worked
        int[][] ordersPerAgent = new int[3][];  // 3 rows, columns not decided yet
        ordersPerAgent[0] = new int[] {5, 7};
        ordersPerAgent[1] = new int[] {3, 4, 6, 2};
        ordersPerAgent[2] = new int[] {8};
        for (int agent = 0; agent < ordersPerAgent.length; agent++) {
            System.out.println("Agent " + agent + " worked " + ordersPerAgent[agent].length
                    + " day(s): " + Arrays.toString(ordersPerAgent[agent]));
        }

        // ---------- 6. Matrix transpose (rows become columns) ----------
        int[][] m = {{1, 2, 3}, {4, 5, 6}};      // 2 x 3
        int[][] t = new int[3][2];               // 3 x 2
        for (int r = 0; r < m.length; r++) {
            for (int c = 0; c < m[r].length; c++) {
                t[c][r] = m[r][c];
            }
        }
        System.out.println("Matrix   : " + Arrays.deepToString(m));
        System.out.println("Transpose: " + Arrays.deepToString(t));
    }
}

// Output:
// Rows (students)   : 3
// Cols (subjects)   : 4
// Student 1, Chem   : 79
// Whole table       : [[78, 85, 62, 90], [92, 88, 79, 84], [55, 67, 71, 60]]
// Student 0 total = 315
// Student 1 total = 343
// Student 2 total = 253
// Maths    average = 75.00
// Physics  average = 80.00
// Chem     average = 70.67
// English  average = 78.00
// 3x3 multiplication grid:
//   1  2  3
//   2  4  6
//   3  6  9
// Agent 0 worked 2 day(s): [5, 7]
// Agent 1 worked 4 day(s): [3, 4, 6, 2]
// Agent 2 worked 1 day(s): [8]
// Matrix   : [[1, 2, 3], [4, 5, 6]]
// Transpose: [[1, 4], [2, 5], [3, 6]]
