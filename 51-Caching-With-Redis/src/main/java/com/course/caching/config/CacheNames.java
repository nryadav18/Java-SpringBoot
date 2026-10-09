package com.course.caching.config;

/*
 * One place for all cache names.
 * WHY: cache names are plain strings. A typo ("prodcuts") silently creates a NEW,
 * separate cache and your @CacheEvict would clear the wrong one. Constants avoid that.
 */
public final class CacheNames {

    /** One entry per product. Key = product id. */
    public static final String PRODUCTS = "products";

    /** One entry per category search. Key = category in lower case. */
    public static final String PRODUCT_SEARCH = "productSearch";

    private CacheNames() {
        // constants only - nobody should create an object of this class
    }
}
