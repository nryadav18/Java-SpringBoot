package com.course.apiclient.model;

/*
 * The JSON that JSONPlaceholder returns for a post:
 *   { "userId": 1, "id": 1, "title": "...", "body": "..." }
 *
 * Jackson maps JSON fields to record components BY NAME.
 * Unknown extra JSON fields are ignored by Spring Boot's default Jackson setup,
 * so the other API can add fields without breaking us.
 */
public record Post(Long userId, Long id, String title, String body) {
}
