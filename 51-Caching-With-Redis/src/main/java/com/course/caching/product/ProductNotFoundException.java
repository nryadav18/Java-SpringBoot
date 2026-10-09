package com.course.caching.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Thrown when a product id does not exist. @ResponseStatus turns it into HTTP 404.
 *
 * Caching note: when a @Cacheable method THROWS, nothing is put into the cache.
 * So "not found" is never cached - the next call asks the database again.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product " + id + " not found");
    }
}
