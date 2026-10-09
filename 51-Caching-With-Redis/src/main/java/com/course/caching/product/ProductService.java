package com.course.caching.product;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.course.caching.config.CacheNames;

/*
 * The heart of this topic: the caching annotations.
 *
 *   @Cacheable  -> "look in the cache first; only run the method on a MISS, then store the result"
 *   @CachePut   -> "ALWAYS run the method, then put the result into the cache" (refresh after an update)
 *   @CacheEvict -> "remove entries from the cache" (after delete / when data becomes stale)
 *   @Caching    -> group several of the above on one method
 *
 * Spring adds this behaviour with an AOP PROXY (topic 27) around this bean.
 * That is why calling a cached method from INSIDE this same class does NOT use the cache
 * (self-invocation - the call never goes through the proxy).
 */
@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * Cache-aside read. Key = the id parameter, e.g. "products::1" in Redis.
     * First call: MISS -> method runs (2 s) -> result stored. Next calls: HIT -> method body is skipped.
     */
    @Cacheable(cacheNames = CacheNames.PRODUCTS, key = "#id")
    public Product getProductById(Long id) {
        log.info("Cache MISS for product {} - loading from the database", id);
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Conditional caching:
     *   condition -> checked BEFORE the call: only cache searches with 3+ characters
     *   unless    -> checked AFTER the call: do not cache empty results
     * The key is normalised to lower case, so "Laptops" and "laptops" share one entry.
     */
    @Cacheable(cacheNames = CacheNames.PRODUCT_SEARCH,
               key = "#category.toLowerCase()",
               condition = "#category.length() >= 3",
               unless = "#result.isEmpty()")
    public List<Product> searchByCategory(String category) {
        log.info("Cache MISS for category '{}' - loading from the database", category);
        return repository.findByCategory(category);
    }

    /**
     * Create: store the new product directly in the cache (key = the generated id, read from #result)
     * and throw away ALL cached search lists, because a list may now be incomplete.
     */
    @Caching(
        put   = @CachePut(cacheNames = CacheNames.PRODUCTS, key = "#result.id"),
        evict = @CacheEvict(cacheNames = CacheNames.PRODUCT_SEARCH, allEntries = true)
    )
    public Product createProduct(String name, String category, BigDecimal price) {
        return repository.save(new Product(null, name, category, price));
    }

    /**
     * Update: @CachePut ALWAYS runs the method and overwrites "products::{id}" with the new value,
     * so readers immediately see the new price. Search lists are evicted (they contain the old price).
     */
    @Caching(
        put   = @CachePut(cacheNames = CacheNames.PRODUCTS, key = "#id"),
        evict = @CacheEvict(cacheNames = CacheNames.PRODUCT_SEARCH, allEntries = true)
    )
    public Product updatePrice(Long id, BigDecimal newPrice) {
        // NOTE: we read from the repository directly, NOT this.getProductById(id).
        // A call through "this" bypasses the proxy, so the cache would not be used anyway.
        Product existing = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        return repository.save(existing.withPrice(newPrice));
    }

    /** Delete: remove the single entry AND all search lists. */
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheNames.PRODUCTS, key = "#id"),
        @CacheEvict(cacheNames = CacheNames.PRODUCT_SEARCH, allEntries = true)
    })
    public void deleteProduct(Long id) {
        if (!repository.deleteById(id)) {
            throw new ProductNotFoundException(id);
        }
    }

    /** How many times the slow "database" was really called. */
    public int databaseHits() {
        return repository.getDatabaseHits();
    }
}
