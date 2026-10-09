package com.course.apiclient.model;

/*
 * We only declare the fields WE need. JSONPlaceholder also sends address,
 * phone, website, company ... - Jackson simply ignores them.
 */
public record User(Long id, String name, String username, String email) {
}
