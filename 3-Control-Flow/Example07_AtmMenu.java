/*
 * ============================================================
 *  Topic 3 - Example 07 : Mini-program - ATM Menu
 * ============================================================
 *  What this file demonstrates (everything from this topic working together):
 *    - A do-while loop that keeps showing a menu until the user chooses Exit.
 *    - A switch expression / arrow switch to handle the choice.
 *    - if / else for validation (insufficient balance, invalid amounts).
 *    - A PIN check with a limited number of attempts (while loop + break).
 *
 *  How to run:
 *    java Example07_AtmMenu.java
 *  The PIN is 1234.
 *
 *  Sample run (what you type is after the "> " prompts):
 *    Enter PIN > 1111
 *    Wrong PIN. Attempts left: 2
 *    Enter PIN > 1234
 *    Welcome, Kiran!
 *    ...menu... Choose an option > 1
 *    Your balance is: 5000.00
 *    ...menu... Choose an option > 2
 *    Amount to deposit > 1500
 *    Deposited 1500.00. New balance: 6500.00
 *    ...menu... Choose an option > 3
 *    Amount to withdraw > 10000
 *    Insufficient balance! You have 6500.00
 *    ...menu... Choose an option > 3
 *    Amount to withdraw > 2000
 *    Please collect your cash: 2000.00. New balance: 4500.00
 *    ...menu... Choose an option > 4
 *    Thank you for banking with us. Goodbye!
 * ============================================================
 */
import java.util.Scanner;

public class Example07_AtmMenu {

    static final int CORRECT_PIN = 1234;
    static final int MAX_PIN_ATTEMPTS = 3;
    static final double DAILY_WITHDRAW_LIMIT = 20000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double balance = 5000.00;
        String customerName = "Kiran";

        // ---------- Step 1: PIN check with limited attempts ----------
        boolean authenticated = false;
        int attemptsLeft = MAX_PIN_ATTEMPTS;
        while (attemptsLeft > 0) {
            System.out.print("Enter PIN > ");
            int pin = scanner.nextInt();
            if (pin == CORRECT_PIN) {
                authenticated = true;
                break;                              // correct PIN: stop asking
            }
            attemptsLeft--;
            System.out.println("Wrong PIN. Attempts left: " + attemptsLeft);
        }

        if (!authenticated) {
            System.out.println("Card blocked. Please contact your bank.");
            scanner.close();
            return;                                 // 'return' in main ends the program
        }

        System.out.println("Welcome, " + customerName + "!");

        // ---------- Step 2: the menu loop ----------
        // do-while: show the menu at least once, repeat until the user picks 4.
        int choice;
        do {
            System.out.println();
            System.out.println("===== ATM MENU =====");
            System.out.println("1. Check balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Exit");
            System.out.print("Choose an option > ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> System.out.printf("Your balance is: %.2f%n", balance);

                case 2 -> {
                    System.out.print("Amount to deposit > ");
                    double amount = scanner.nextDouble();
                    if (amount <= 0) {
                        System.out.println("Amount must be positive.");
                    } else {
                        balance += amount;
                        System.out.printf("Deposited %.2f. New balance: %.2f%n", amount, balance);
                    }
                }

                case 3 -> {
                    System.out.print("Amount to withdraw > ");
                    double amount = scanner.nextDouble();
                    if (amount <= 0) {
                        System.out.println("Amount must be positive.");
                    } else if (amount > DAILY_WITHDRAW_LIMIT) {
                        System.out.printf("Daily limit is %.2f%n", DAILY_WITHDRAW_LIMIT);
                    } else if (amount > balance) {
                        System.out.printf("Insufficient balance! You have %.2f%n", balance);
                    } else if (amount % 100 != 0) {
                        System.out.println("Please enter a multiple of 100.");
                    } else {
                        balance -= amount;
                        System.out.printf("Please collect your cash: %.2f. New balance: %.2f%n",
                                amount, balance);
                    }
                }

                case 4 -> System.out.println("Thank you for banking with us. Goodbye!");

                default -> System.out.println("Invalid option. Please choose 1-4.");
            }
        } while (choice != 4);

        scanner.close();
    }
}
