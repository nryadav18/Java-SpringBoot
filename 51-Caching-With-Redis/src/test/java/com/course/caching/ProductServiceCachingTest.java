package com.course.caching;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import com.course.caching.product.Product;
import com.course.caching.product.ProductService;

/*
 * Proves that caching works by counting REAL database hits.
 * The fake database delay is set to 0 ms so the test is fast.
 * Run with:  mvn test
 */
@SpringBootTest(properties = "app.slow-database.delay-ms=0")
class ProductServiceCachingTest {

    @Autowired
    ProductService productService;

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
    }

    @Test
    void secondReadComesFromTheCache() {
        int before = productService.databaseHits();

        productService.getProductById(1L);   // MISS -> database
        productService.getProductById(1L);   // HIT  -> no database
        productService.getProductById(1L);   // HIT  -> no database

        assertThat(productService.databaseHits() - before).isEqualTo(1);
    }

    @Test
    void cachePutRefreshesTheCachedValue() {
        productService.getProductById(2L);                                   // cache it
        productService.updatePrice(2L, new BigDecimal("1000.00"));           // @CachePut overwrites it
        int before = productService.databaseHits();

        Product fromCache = productService.getProductById(2L);

        assertThat(fromCache.price()).isEqualByComparingTo("1000.00");     // new price ...
        assertThat(productService.databaseHits()).isEqualTo(before);       // ... and still a cache HIT
    }

    @Test
    void shortSearchTermsAreNotCached() {
        int before = productService.databaseHits();

        productService.searchByCategory("ph");   // condition false (< 3 chars) -> never cached
        productService.searchByCategory("ph");

        assertThat(productService.databaseHits() - before).isEqualTo(2);
    }
}
