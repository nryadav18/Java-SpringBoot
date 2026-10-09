package com.course.graphql.library;

/*
 * Matches "type Author" in schema.graphqls.
 * Spring for GraphQL reads record components by name: id -> id, name -> name ...
 * The "books" field is NOT here - it is resolved separately by @SchemaMapping.
 */
public record Author(Long id, String name, String country) {
}
