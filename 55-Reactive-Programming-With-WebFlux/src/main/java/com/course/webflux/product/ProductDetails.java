package com.course.webflux.product;

/*
 * A small DTO (record) that COMBINES two results: the product and its stock.
 * Built in ProductService.details() using Mono.zip(...).
 */
public record ProductDetails(Product product, int stock) {
}
