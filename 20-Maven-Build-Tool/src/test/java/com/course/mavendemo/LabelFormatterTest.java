package com.course.mavendemo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/*
 * LabelFormatterTest.java
 * ------------------------------------------------------------
 * A unit test. It lives in src/test/java (same package as the class it tests).
 * Maven's 'test' phase (surefire plugin) finds classes ending in *Test and runs them.
 * If any test fails, 'mvn package' STOPS - a broken JAR is never built.
 *
 * Don't worry about the details yet: Topic 21 teaches JUnit 5 properly.
 */
class LabelFormatterTest {

    private final LabelFormatter formatter = new LabelFormatter();

    @Test
    void slugIsLowerCaseWithDashes() {
        assertEquals("wireless-mouse-black", formatter.toSlug("  Wireless   Mouse (Black) "));
    }

    @Test
    void slugOfBlankTextIsEmpty() {
        assertEquals("", formatter.toSlug("   "));
        assertEquals("", formatter.toSlug(null));
    }

    @Test
    void productCodeUsesFirstThreeLettersAndPaddedId() {
        assertEquals("MOU-00042", formatter.productCode("mouse pad", 42));
    }

    @Test
    void shortTitleIsCutWithDots() {
        assertEquals("Ergonomic wi...", formatter.shortTitle("ergonomic wireless mouse", 15));
    }
}
