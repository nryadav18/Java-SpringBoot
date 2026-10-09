package com.course.patterns.demo;

/*
 * PatternDemo.java - every demo in this project implements this small interface.
 *
 * DemoRunner asks Spring for "all beans of type PatternDemo" (a List<PatternDemo>)
 * and runs them in @Order. Adding a new demo = adding a new class. DemoRunner never changes.
 * That is the Open/Closed Principle in action (see solid/ocp).
 */
public interface PatternDemo {

    /** Heading printed before the demo, e.g. "S - Single Responsibility Principle". */
    String title();

    /** Runs the demo and prints what happens. */
    void run();
}
