/*
 * ============================================================
 *  Topic 3 - Example 05 : Nested loops and patterns
 * ============================================================
 *  What this file demonstrates:
 *    - A loop inside a loop: outer loop = ROWS, inner loop = COLUMNS.
 *    - Classic star / number patterns (very common in interviews and tests).
 *
 *  How to run:
 *    java Example05_NestedLoopsPatterns.java
 * ============================================================
 */
public class Example05_NestedLoopsPatterns {

    public static void main(String[] args) {
        int n = 4;

        // ---------- 1. Rectangle ----------
        // For EACH row (outer), print n stars (inner), then go to a new line.
        System.out.println("1) Rectangle 3 x 5");
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 5; col++) {
                System.out.print("* ");
            }
            System.out.println();      // end of the row
        }

        // ---------- 2. Right triangle ----------
        // Row 1 has 1 star, row 2 has 2 stars... the inner loop depends on the outer one.
        System.out.println("2) Right triangle");
        for (int row = 1; row <= n; row++) {
            for (int col = 1; col <= row; col++) {
                System.out.print("* ");
            }
            System.out.println();
        }

        // ---------- 3. Inverted triangle ----------
        System.out.println("3) Inverted triangle");
        for (int row = n; row >= 1; row--) {
            for (int col = 1; col <= row; col++) {
                System.out.print("* ");
            }
            System.out.println();
        }

        // ---------- 4. Number triangle ----------
        System.out.println("4) Number triangle");
        for (int row = 1; row <= n; row++) {
            for (int col = 1; col <= row; col++) {
                System.out.print(col + " ");
            }
            System.out.println();
        }

        // ---------- 5. Pyramid ----------
        // Each row: (n - row) spaces, then (2*row - 1) stars.
        System.out.println("5) Pyramid");
        for (int row = 1; row <= n; row++) {
            for (int space = 1; space <= n - row; space++) {
                System.out.print(" ");
            }
            for (int star = 1; star <= 2 * row - 1; star++) {
                System.out.print("*");
            }
            System.out.println();
        }

        // ---------- 6. Floyd's triangle (a counter that keeps going) ----------
        System.out.println("6) Floyd's triangle");
        int counter = 1;
        for (int row = 1; row <= n; row++) {
            for (int col = 1; col <= row; col++) {
                System.out.print(counter + " ");
                counter++;
            }
            System.out.println();
        }
    }
}

// Output:
// 1) Rectangle 3 x 5
// * * * * *
// * * * * *
// * * * * *
// 2) Right triangle
// *
// * *
// * * *
// * * * *
// 3) Inverted triangle
// * * * *
// * * *
// * *
// *
// 4) Number triangle
// 1
// 1 2
// 1 2 3
// 1 2 3 4
// 5) Pyramid
//    *
//   ***
//  *****
// *******
// 6) Floyd's triangle
// 1
// 2 3
// 4 5 6
// 7 8 9 10
