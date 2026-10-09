package com.course.graphql.library;

/*
 * Matches "input BookInput" in schema.graphqls.
 * @Argument BookInput input  -> Spring converts the GraphQL input object into this record.
 */
public record BookInput(String title, int pages, Long authorId) {
}
