package com.course.graphql.library;

/*
 * Matches "type Book" in schema.graphqls.
 *
 * Notice: we store only authorId (like a foreign key in a database).
 * The schema field "author: Author!" does not exist on this record, so Spring
 * calls our @BatchMapping method in BookController to load it.
 */
public record Book(Long id, String title, int pages, Long authorId) {
}
