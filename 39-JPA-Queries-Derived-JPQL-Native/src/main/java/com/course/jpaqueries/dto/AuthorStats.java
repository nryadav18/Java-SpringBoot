package com.course.jpaqueries.dto;

/*
 * AuthorStats - a CLASS-BASED (DTO / record) PROJECTION.
 * ------------------------------------------------------
 * Filled by a JPQL "constructor expression":
 *   SELECT new com.course.jpaqueries.dto.AuthorStats(b.author, COUNT(b), AVG(b.price)) ...
 * The constructor parameter types must match what JPQL returns:
 *   COUNT(...) -> Long,   AVG(...) -> Double
 */
public record AuthorStats(String author, Long bookCount, Double averagePrice) {
}
