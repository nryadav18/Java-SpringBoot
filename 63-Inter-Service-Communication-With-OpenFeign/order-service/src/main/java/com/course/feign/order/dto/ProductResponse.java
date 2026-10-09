/*
 * ProductResponse - OUR copy of the product data we receive from product-service.
 * Each microservice owns its own classes; we do NOT share a jar with product-service
 * (sharing model jars couples services together - see topic 59).
 * Field names must match the JSON keys ("id", "name", "price", "stock").
 * Unknown extra JSON fields are ignored by Spring Boot's Jackson defaults.
 */
package com.course.feign.order.dto;

public record ProductResponse(Long id, String name, double price, int stock) {
}
