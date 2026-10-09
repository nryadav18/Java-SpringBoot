package com.course.jpaqueries.dto;

/*
 * GenreCount - an interface projection used with a NATIVE SQL query.
 * The SQL column ALIASES ("AS genre", "AS total") must match the getter names.
 */
public interface GenreCount {

    String getGenre();

    long getTotal();
}
