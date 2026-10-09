package com.course.caching.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Small "admin" endpoints to LOOK INTO the caches while learning.
 *
 * CacheManager is the object that owns all caches. Spring Boot creates the right one for us:
 *   - ConcurrentMapCacheManager in the default profile
 *   - RedisCacheManager         in the "redis" profile
 *
 * (In a real application, protect endpoints like these with Spring Security - topic 47!)
 */
@RestController
@RequestMapping("/api/caches")
public class CacheAdminController {

    private final CacheManager cacheManager;

    public CacheAdminController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /** Shows the cache manager type, every cache name and (for the in-memory cache) its keys. */
    @GetMapping
    public Map<String, Object> listCaches() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cacheManager", cacheManager.getClass().getSimpleName());
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            Object nativeCache = cache == null ? null : cache.getNativeCache();
            if (nativeCache instanceof ConcurrentMap<?, ?> map) {          // pattern matching for instanceof (Java 16+)
                result.put(name, map.keySet());
            } else {
                result.put(name, "stored in Redis - inspect with: docker exec -it course-redis redis-cli KEYS '*'");
            }
        }
        return result;
    }

    /** Clears ONE cache, e.g. DELETE /api/caches/products */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> clearOne(@PathVariable String name) {
        Cache cache = cacheManager.getCache(name);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        cache.clear();
        return ResponseEntity.noContent().build();
    }

    /** Clears ALL caches. */
    @DeleteMapping
    public ResponseEntity<Void> clearAll() {
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        return ResponseEntity.noContent().build();
    }
}
