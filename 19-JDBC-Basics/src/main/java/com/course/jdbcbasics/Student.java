package com.course.jdbcbasics;

/*
 * Student.java
 * ------------------------------------------------------------
 * A simple data carrier (a record - Topic 15) for ONE row of the "students" table.
 *
 *   Table column  ->  Java field
 *   id    INT          Integer id   (Integer, not int, so it can be null BEFORE the DB gives us an id)
 *   name  VARCHAR      String name
 *   email VARCHAR      String email
 *   marks INT          int marks
 *
 * Converting rows <-> objects by hand is exactly the boring work that
 * JPA/Hibernate (Topic 38) will later do for us automatically.
 */
public record Student(Integer id, String name, String email, int marks) {

    /** Convenience constructor for a NEW student that has no id yet. */
    public Student(String name, String email, int marks) {
        this(null, name, email, marks);
    }
}
