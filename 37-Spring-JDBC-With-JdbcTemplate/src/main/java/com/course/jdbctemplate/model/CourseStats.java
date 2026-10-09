package com.course.jdbctemplate.model;

/*
 * Result of an aggregate query (GROUP BY). It does not match any table -
 * JDBC lets you map ANY result set to ANY object.
 */
public record CourseStats(String course, long students, double averageMarks) {
}
