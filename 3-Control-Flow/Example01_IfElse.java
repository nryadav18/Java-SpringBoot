/*
 * ============================================================
 *  Topic 3 - Example 01 : if / else if / else, nested if, ternary
 * ============================================================
 *  What this file demonstrates:
 *    - A simple if.
 *    - if / else.
 *    - An if / else if / else ladder (grade calculator).
 *    - Nested if (loan eligibility).
 *    - Combining conditions with && and ||.
 *    - The ternary operator  condition ? a : b
 *
 *  How to run:
 *    java Example01_IfElse.java
 * ============================================================
 */
public class Example01_IfElse {

    public static void main(String[] args) {

        // ---------- 1. simple if ----------
        // The block runs ONLY when the condition is true. Otherwise it is skipped.
        double cartTotal = 650.0;
        if (cartTotal >= 500) {
            System.out.println("You get FREE delivery!");
        }

        // ---------- 2. if / else ----------
        // Exactly ONE of the two blocks runs.
        int age = 16;
        if (age >= 18) {
            System.out.println("You can vote.");
        } else {
            System.out.println("You cannot vote yet. Wait " + (18 - age) + " more years.");
        }

        // ---------- 3. if / else if / else ladder ----------
        // Java checks conditions from TOP to BOTTOM and runs the FIRST one that is true.
        // All the rest are skipped. The final else is the "none of the above" case.
        int marks = 82;
        char grade;
        if (marks >= 90) {
            grade = 'A';
        } else if (marks >= 75) {     // we only get here if marks < 90
            grade = 'B';
        } else if (marks >= 60) {
            grade = 'C';
        } else if (marks >= 40) {
            grade = 'D';
        } else {
            grade = 'F';
        }
        System.out.println("Marks " + marks + " -> grade " + grade);

        // ---------- 4. nested if ----------
        // An if inside another if: the inner check happens only if the outer one passed.
        int applicantAge = 30;
        double monthlySalary = 45000;
        int creditScore = 720;
        if (applicantAge >= 21 && applicantAge <= 60) {
            if (monthlySalary >= 25000) {
                if (creditScore >= 700) {
                    System.out.println("Loan APPROVED");
                } else {
                    System.out.println("Loan rejected: credit score too low");
                }
            } else {
                System.out.println("Loan rejected: salary too low");
            }
        } else {
            System.out.println("Loan rejected: age not in 21-60");
        }

        // ---------- 5. combining conditions ----------
        int year = 2024;
        // Leap year rule: divisible by 4 AND not by 100, OR divisible by 400.
        boolean isLeap = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
        if (isLeap) {
            System.out.println(year + " is a leap year");
        } else {
            System.out.println(year + " is not a leap year");
        }

        // ---------- 6. ternary operator ----------
        // condition ? valueIfTrue : valueIfFalse   -> a compact if/else that PRODUCES a value.
        int stock = 0;
        String status = stock > 0 ? "In stock" : "Out of stock";
        System.out.println("Status: " + status);

        int a = 12, b = 7;
        int bigger = (a > b) ? a : b;
        System.out.println("Bigger of " + a + " and " + b + " is " + bigger);

        int items = 1;
        System.out.println("You have " + items + (items == 1 ? " item" : " items") + " in your cart");
    }
}

// Output:
// You get FREE delivery!
// You cannot vote yet. Wait 2 more years.
// Marks 82 -> grade B
// Loan APPROVED
// 2024 is a leap year
// Status: Out of stock
// Bigger of 12 and 7 is 12
// You have 1 item in your cart
