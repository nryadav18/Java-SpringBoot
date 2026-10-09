package com.course.unittesting;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/*
 * CalculatorTest.java
 * ------------------------------------------------------------
 * JUnit 5 basics on a simple class:
 *   @Test, assertions, @BeforeEach/@AfterEach, @BeforeAll/@AfterAll,
 *   @DisplayName, @Nested, @ParameterizedTest (@ValueSource, @CsvSource),
 *   assertThrows, assertAll, and the AAA pattern (Arrange - Act - Assert).
 *
 * Run:  mvn test -Dtest=CalculatorTest
 *
 * Notes:
 *  - Test classes and methods do NOT need to be public in JUnit 5.
 *  - JUnit creates a NEW CalculatorTest object for EVERY test method,
 *    so tests can't accidentally share state through fields.
 */
@DisplayName("Calculator")
class CalculatorTest {

    private Calculator calculator;

    // ---------- Lifecycle methods ----------
    @BeforeAll                       // once, before ALL tests in this class (must be static)
    static void beforeAll() {
        System.out.println("[CalculatorTest] starting - e.g. open an expensive shared resource here");
    }

    @BeforeEach                      // before EACH test: fresh object -> tests are independent
    void setUp() {
        calculator = new Calculator();
    }

    @AfterEach                       // after EACH test: clean up (close files, clear data...)
    void tearDown() {
        calculator = null;
    }

    @AfterAll                        // once, after ALL tests (must be static)
    static void afterAll() {
        System.out.println("[CalculatorTest] finished");
    }

    // ---------- Simple tests ----------
    @Test
    @DisplayName("2 + 3 = 5")
    void addsTwoNumbers() {
        // Arrange (prepare inputs)
        int a = 2;
        int b = 3;

        // Act (call the method under test)
        int result = calculator.add(a, b);

        // Assert (check the outcome) - expected value FIRST, actual SECOND
        assertEquals(5, result);
    }

    @Test
    void subtractsAndCanGoNegative() {
        assertEquals(-4, calculator.subtract(3, 7));
    }

    @Test
    void multiplyByZeroIsZero() {
        assertEquals(0, calculator.multiply(12345, 0), "anything times zero must be zero");
    }

    @Test
    void checksEvenAndOddNumbers() {
        assertTrue(calculator.isEven(10));
        assertFalse(calculator.isEven(7));
    }

    @Test
    void percentageOfAmount() {
        // doubles: give a tolerance (delta) because of floating-point rounding
        assertEquals(30.0, calculator.percentage(200, 15), 0.0001);
    }

    // ---------- Exceptions ----------
    @Test
    @DisplayName("dividing by zero throws IllegalArgumentException")
    void divideByZeroThrows() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.divide(10, 0));       // the code that SHOULD throw, as a lambda

        assertEquals("Cannot divide by zero", ex.getMessage());
    }

    // ---------- assertAll: check several things, see ALL failures at once ----------
    @Test
    void divisionResults() {
        assertAll("division",
                () -> assertEquals(5, calculator.divide(10, 2)),
                () -> assertEquals(3, calculator.divide(10, 3)),    // integer division drops .33
                () -> assertEquals(-2, calculator.divide(-10, 5)));
    }

    // ---------- Parameterized tests: one test method, many inputs ----------
    @ParameterizedTest(name = "{0} is even")
    @ValueSource(ints = {0, 2, 4, 100, -8})
    void evenNumbers(int number) {
        assertTrue(calculator.isEven(number));
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1,   1,   2",
            "-5,  5,   0",
            "100, 250, 350",
            "0,   0,   0"
    })
    void addsManyPairs(int a, int b, int expected) {
        assertEquals(expected, calculator.add(a, b));
    }

    @ParameterizedTest(name = "percent {0} is rejected")
    @ValueSource(doubles = {-1, 100.5, 250})
    void invalidPercentagesAreRejected(double percent) {
        assertThrows(IllegalArgumentException.class, () -> calculator.percentage(100, percent));
    }

    // ---------- @Nested: group related tests ----------
    @Nested
    @DisplayName("when dividing")
    class Dividing {

        @Test
        @DisplayName("an exact division has no remainder")
        void exactDivision() {
            assertEquals(4, calculator.divide(20, 5));
        }

        @Test
        @DisplayName("a negative divisor gives a negative result")
        void negativeDivisor() {
            assertEquals(-4, calculator.divide(20, -5));
        }
    }
}
