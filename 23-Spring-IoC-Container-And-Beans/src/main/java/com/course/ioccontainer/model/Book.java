/*
 * A Book in our little library.
 *
 * This is a Java RECORD (Topic 15): an immutable data carrier.
 * It is NOT a Spring bean - it is plain data. Beans are the "workers"
 * (repositories, services, controllers); records like this are the "things"
 * the workers handle. We create data objects with "new" freely.
 */
package com.course.ioccontainer.model;

public record Book(String isbn, String title, String author, boolean available) {
}
