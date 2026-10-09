package com.course.unittesting;

/*
 * Calculator.java
 * ------------------------------------------------------------
 * The simplest possible "class under test". No dependencies, pure logic:
 * perfect for learning JUnit basics (see CalculatorTest).
 */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    /** Integer division. Dividing by zero is not allowed. */
    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }

    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    /** Returns 'percent' % of 'amount', e.g. percentage(200, 15) = 30.0 */
    public double percentage(double amount, double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Percent must be between 0 and 100 but was " + percent);
        }
        return amount * percent / 100;
    }
}
