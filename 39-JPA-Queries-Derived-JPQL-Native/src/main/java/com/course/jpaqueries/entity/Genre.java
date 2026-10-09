package com.course.jpaqueries.entity;

/*
 * Book genres. Stored in the database as TEXT ("FANTASY") thanks to @Enumerated(EnumType.STRING)
 * on the Book.genre field.
 */
public enum Genre {
    FICTION, FANTASY, SCIENCE, HISTORY, PROGRAMMING
}
