/*
 * ============================================================
 *  Topic 3 - Example 06 : Common loop problems
 * ============================================================
 *  What this file demonstrates the classic beginner / interview loop programs:
 *    - Sum of 1..N
 *    - Factorial
 *    - Prime check
 *    - FizzBuzz
 *    - Multiplication table
 *    - Reverse a number and sum its digits
 *    - Fibonacci series
 *
 *  How to run:
 *    java Example06_CommonLoopProblems.java
 * ============================================================
 */
public class Example06_CommonLoopProblems {

    public static void main(String[] args) {

        // ---------- 1. Sum of 1..N ----------
        // The "accumulator" pattern: start at 0, add something each round.
        int n = 10;
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        System.out.println("1) Sum of 1.." + n + " = " + sum);

        // ---------- 2. Factorial: 5! = 5 x 4 x 3 x 2 x 1 ----------
        // Start at 1 (not 0!) because we MULTIPLY. Use long: factorials grow very fast.
        int f = 5;
        long factorial = 1;
        for (int i = 2; i <= f; i++) {
            factorial *= i;
        }
        System.out.println("2) " + f + "! = " + factorial);

        // ---------- 3. Prime check ----------
        // A prime has exactly two divisors: 1 and itself. 2, 3, 5, 7, 11, 13...
        // Trick: we only need to test divisors up to the square root of the number.
        int candidate = 29;
        boolean isPrime = candidate > 1;          // 0 and 1 are not prime
        for (int d = 2; d * d <= candidate; d++) {
            if (candidate % d == 0) {
                isPrime = false;
                break;                            // one divisor is enough to say "not prime"
            }
        }
        System.out.println("3) " + candidate + " is prime? " + isPrime);

        // All primes up to 30 (a loop inside a loop)
        System.out.print("   Primes up to 30: ");
        for (int num = 2; num <= 30; num++) {
            boolean prime = true;
            for (int d = 2; d * d <= num; d++) {
                if (num % d == 0) {
                    prime = false;
                    break;
                }
            }
            if (prime) {
                System.out.print(num + " ");
            }
        }
        System.out.println();

        // ---------- 4. FizzBuzz ----------
        // 1..15: multiples of 3 -> "Fizz", of 5 -> "Buzz", of both -> "FizzBuzz".
        // Check the "both" case FIRST, otherwise 15 would print just "Fizz".
        System.out.print("4) FizzBuzz: ");
        for (int i = 1; i <= 15; i++) {
            if (i % 15 == 0) {
                System.out.print("FizzBuzz ");
            } else if (i % 3 == 0) {
                System.out.print("Fizz ");
            } else if (i % 5 == 0) {
                System.out.print("Buzz ");
            } else {
                System.out.print(i + " ");
            }
        }
        System.out.println();

        // ---------- 5. Multiplication table ----------
        int table = 7;
        System.out.println("5) Table of " + table + ":");
        for (int i = 1; i <= 10; i++) {
            System.out.printf("   %d x %2d = %2d%n", table, i, table * i);
        }

        // ---------- 6. Reverse a number and sum its digits ----------
        // Repeat: take last digit (% 10), use it, then remove it (/ 10).
        int original = 4728;
        int remaining = original;
        int reversed = 0;
        int digitSum = 0;
        while (remaining > 0) {
            int lastDigit = remaining % 10;
            reversed = reversed * 10 + lastDigit;
            digitSum += lastDigit;
            remaining /= 10;
        }
        System.out.println("6) Reverse of " + original + " = " + reversed + ", digit sum = " + digitSum);
        boolean isPalindrome = (12321 == reverseOf(12321));
        System.out.println("   12321 is a palindrome number? " + isPalindrome);

        // ---------- 7. Fibonacci series ----------
        // Each number = sum of the two before it: 0 1 1 2 3 5 8 13 ...
        System.out.print("7) Fibonacci (10 terms): ");
        long first = 0, second = 1;
        for (int i = 1; i <= 10; i++) {
            System.out.print(first + " ");
            long next = first + second;
            first = second;
            second = next;
        }
        System.out.println();
    }

    // A small helper (methods are explained in Topic 4 - for now: a reusable block of code).
    static int reverseOf(int number) {
        int reversed = 0;
        while (number > 0) {
            reversed = reversed * 10 + number % 10;
            number /= 10;
        }
        return reversed;
    }
}

// Output:
// 1) Sum of 1..10 = 55
// 2) 5! = 120
// 3) 29 is prime? true
//    Primes up to 30: 2 3 5 7 11 13 17 19 23 29
// 4) FizzBuzz: 1 2 Fizz 4 Buzz Fizz 7 8 Fizz Buzz 11 Fizz 13 14 FizzBuzz
// 5) Table of 7:
//    7 x  1 =  7
//    7 x  2 = 14
//    7 x  3 = 21
//    7 x  4 = 28
//    7 x  5 = 35
//    7 x  6 = 42
//    7 x  7 = 49
//    7 x  8 = 56
//    7 x  9 = 63
//    7 x 10 = 70
// 6) Reverse of 4728 = 8274, digit sum = 21
//    12321 is a palindrome number? true
// 7) Fibonacci (10 terms): 0 1 1 2 3 5 8 13 21 34
