package com.course.unittesting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/*
 * AssertJExamplesTest.java
 * ------------------------------------------------------------
 * AssertJ = an assertion library with a FLUENT style:
 *     assertThat(actual).isEqualTo(expected)
 * It reads like English, your IDE auto-completes the next check after the dot,
 * and failure messages are very clear. Spring Boot's test starter includes it,
 * so you will see it a lot in real projects.
 *
 * Run:  mvn test -Dtest=AssertJExamplesTest
 */
@DisplayName("AssertJ examples")
class AssertJExamplesTest {

    record Student(String name, int marks) { }

    @Test
    void numbersAndStrings() {
        Calculator calculator = new Calculator();

        assertThat(calculator.add(2, 3)).isEqualTo(5).isPositive().isLessThan(10);

        String greeting = "Hello, Spring Boot";
        assertThat(greeting)
                .startsWith("Hello")
                .contains("Spring")
                .endsWith("Boot")
                .hasSize(18);
    }

    @Test
    void collections() {
        List<String> fruits = List.of("apple", "banana", "cherry");

        assertThat(fruits)
                .hasSize(3)
                .contains("banana")
                .doesNotContain("mango")
                .containsExactly("apple", "banana", "cherry");   // same order

        Map<String, Integer> stock = Map.of("pen", 10, "book", 0);
        assertThat(stock).containsEntry("pen", 10).containsKey("book");
    }

    @Test
    void objectsInLists() {
        List<Student> students = List.of(
                new Student("Asha", 91),
                new Student("Ben", 72));

        // extract a property from every element and check it
        assertThat(students)
                .extracting(Student::name)
                .containsExactly("Asha", "Ben");

        assertThat(students)
                .extracting(Student::name, Student::marks)
                .contains(tuple("Asha", 91));

        assertThat(students).allMatch(s -> s.marks() > 50, "everyone passed");
    }

    @Test
    void exceptions() {
        Calculator calculator = new Calculator();

        assertThatThrownBy(() -> calculator.divide(1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot divide by zero");
    }
}
