package com.course.loggingdemo;

/*
 * A simple immutable order (a Java record, topic 15).
 * Note: there is NO card number in here - we never keep or log sensitive data we do not need.
 */
public record Order(long id, String customer, String item, int quantity, String status) {
}
