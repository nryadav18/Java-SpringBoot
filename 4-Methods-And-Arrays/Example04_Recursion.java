/*
 * ============================================================
 *  Topic 4 - Example 04 : Recursion
 * ============================================================
 *  What this file demonstrates:
 *    - A recursive method = a method that calls ITSELF on a smaller problem.
 *    - Every recursion needs a BASE CASE (when to stop) and a RECURSIVE CASE (smaller step).
 *    - factorial(n), fibonacci(n), sum of digits, countdown.
 *    - Tracing calls with indentation to SEE the call stack grow and shrink.
 *    - Why naive recursive Fibonacci is slow, and the iterative alternative.
 *
 *  How to run:
 *    java Example04_Recursion.java
 * ============================================================
 */
public class Example04_Recursion {

    // ---------- 1. Countdown ----------
    static void countdown(int n) {
        if (n == 0) {                       // BASE CASE: stop here
            System.out.println("Go!");
            return;
        }
        System.out.print(n + " ");
        countdown(n - 1);                   // RECURSIVE CASE: same problem, smaller n
    }

    // ---------- 2. Factorial: n! = n * (n-1)! and 0! = 1 ----------
    static long factorial(int n) {
        if (n <= 1) {                       // base case
            return 1;
        }
        return n * factorial(n - 1);        // recursive case
    }

    // Same factorial, but printing each call so we can watch the call stack.
    static long factorialTraced(int n, String indent) {
        System.out.println(indent + "factorial(" + n + ") called");
        if (n <= 1) {
            System.out.println(indent + "factorial(" + n + ") returns 1   <- base case");
            return 1;
        }
        long result = n * factorialTraced(n - 1, indent + "   ");
        System.out.println(indent + "factorial(" + n + ") returns " + result);
        return result;
    }

    // ---------- 3. Fibonacci: fib(n) = fib(n-1) + fib(n-2), fib(0)=0, fib(1)=1 ----------
    static int callCount = 0;               // counts how many times fib is called (to show the cost)

    static long fibonacci(int n) {
        callCount++;
        if (n <= 1) {                       // two base cases: fib(0)=0, fib(1)=1
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);   // TWO recursive calls -> grows very fast!
    }

    // The loop version: fast and uses almost no memory.
    static long fibonacciIterative(int n) {
        long previous = 0, current = 1;
        if (n == 0) {
            return 0;
        }
        for (int i = 2; i <= n; i++) {
            long next = previous + current;
            previous = current;
            current = next;
        }
        return current;
    }

    // ---------- 4. Sum of digits: sumDigits(4728) = 8 + sumDigits(472) ----------
    static int sumDigits(int n) {
        if (n < 10) {
            return n;
        }
        return n % 10 + sumDigits(n / 10);
    }

    public static void main(String[] args) {
        System.out.print("Countdown: ");
        countdown(5);

        System.out.println("5! = " + factorial(5));
        System.out.println("20! = " + factorial(20));   // largest factorial that fits in a long

        System.out.println("--- Tracing factorial(4) ---");
        factorialTraced(4, "");

        System.out.print("Fibonacci 0..10: ");
        for (int i = 0; i <= 10; i++) {
            System.out.print(fibonacci(i) + " ");
        }
        System.out.println();

        callCount = 0;
        long fib30 = fibonacci(30);
        System.out.println("fib(30) = " + fib30 + " took " + callCount + " recursive calls!");
        System.out.println("fib(30) iterative = " + fibonacciIterative(30) + " (just a 29-step loop)");

        System.out.println("Sum of digits of 4728 = " + sumDigits(4728));

        // Without a base case, the method would call itself forever until the stack memory runs out:
        //   -> java.lang.StackOverflowError
    }
}

// Output:
// Countdown: 5 4 3 2 1 Go!
// 5! = 120
// 20! = 2432902008176640000
// --- Tracing factorial(4) ---
// factorial(4) called
//    factorial(3) called
//       factorial(2) called
//          factorial(1) called
//          factorial(1) returns 1   <- base case
//       factorial(2) returns 2
//    factorial(3) returns 6
// factorial(4) returns 24
// Fibonacci 0..10: 0 1 1 2 3 5 8 13 21 34 55
// fib(30) = 832040 took 2692537 recursive calls!
// fib(30) iterative = 832040 (just a 29-step loop)
// Sum of digits of 4728 = 21
