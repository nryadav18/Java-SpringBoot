package com.course.jpaqueries.dto;

/*
 * BookSummary - an INTERFACE-BASED PROJECTION.
 * --------------------------------------------
 * We only declare getters. Spring Data creates an object implementing this interface,
 * and Hibernate SELECTs ONLY these two columns (title, author) instead of the whole row.
 * Getter names must match entity property names: getTitle() -> "title".
 */
public interface BookSummary {

    String getTitle();

    String getAuthor();
}
