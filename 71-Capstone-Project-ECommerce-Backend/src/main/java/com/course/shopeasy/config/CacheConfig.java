package com.course.shopeasy.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Caching for the product catalog (topic 51).
 *
 * The catalog is read MANY times more often than it changes, so we keep recently read products and the
 * category list in memory. A cache hit costs microseconds; a database query costs milliseconds.
 *
 * Two important details:
 *  1. ConcurrentMapCacheManager = a simple in-memory map per cache. Perfect for one instance. With several
 *     instances behind a load balancer you would switch to Redis (topic 51) so all instances share one cache.
 *  2. TransactionAwareCacheManagerProxy: @CacheEvict inside a @Transactional method (e.g. placing an order
 *     changes stock) is delayed until the transaction COMMITS. If the transaction rolls back, nothing is
 *     evicted; and no other request can re-cache the old value between "evict" and "commit".
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    CacheManager cacheManager() {
        // Passing the names fixes the list of caches ("static" mode): a typo like @Cacheable("prodcts")
        // fails loudly instead of silently creating a new, never-evicted cache.
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(CacheNames.PRODUCTS, CacheNames.CATEGORIES);
        // Our cached methods never return null (they throw ResourceNotFoundException instead).
        cacheManager.setAllowNullValues(false);
        return new TransactionAwareCacheManagerProxy(cacheManager);
    }
}
