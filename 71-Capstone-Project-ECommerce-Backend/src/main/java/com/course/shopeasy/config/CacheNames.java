package com.course.shopeasy.config;

/**
 * Cache names as constants - no "magic strings" spread over the code (topic 70: clean code).
 */
public final class CacheNames {

    /** key = product id, value = ProductResponse */
    public static final String PRODUCTS = "products";

    /** key = SimpleKey.EMPTY (the whole list), value = List of CategoryResponse */
    public static final String CATEGORIES = "categories";

    private CacheNames() {
        // constants only - no instances
    }
}
