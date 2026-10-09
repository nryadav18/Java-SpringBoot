package com.course.caching.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
 * Cache configuration.
 *
 * 1) @EnableCaching switches ON Spring's cache abstraction. Without it, @Cacheable & co.
 *    are simply ignored (no error - the method just runs every time!).
 *
 * 2) Which cache STORE is used is decided by Spring Boot auto-configuration and the property
 *    spring.cache.type:
 *      - default profile  -> spring.cache.type=simple  -> ConcurrentMapCacheManager (a HashMap in the JVM)
 *      - "redis" profile  -> spring.cache.type=redis   -> RedisCacheManager (entries live in the Redis server)
 *
 * 3) The two beans below are only created in the "redis" profile. They tell the
 *    RedisCacheManager HOW to store values (JSON instead of Java serialization) and
 *    HOW LONG to keep them (TTL = time to live).
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Default settings for EVERY Redis cache.
     * NOTE: when you define this bean yourself, the spring.cache.redis.* properties
     * (time-to-live, key-prefix ...) are NOT used any more - this bean wins.
     */
    @Bean
    @Profile("redis")
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                // entries disappear automatically after 10 minutes (stale data cannot live forever)
                .entryTtl(Duration.ofMinutes(10))
                // do not store "null" results - a missing product should be looked up again next time
                .disableCachingNullValues()
                // keys are readable strings in Redis, e.g.  products::1
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                // values are stored as JSON (readable in redis-cli, language-neutral)
                // instead of the default JDK serialization (binary, needs "implements Serializable")
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    /**
     * Per-cache settings: search results change more often, so keep them only 1 minute.
     * Single products keep the 10 minute default from the bean above.
     */
    @Bean
    @Profile("redis")
    public RedisCacheManagerBuilderCustomizer perCacheTtlCustomizer(RedisCacheConfiguration defaults) {
        return builder -> builder
                .withCacheConfiguration(CacheNames.PRODUCT_SEARCH, defaults.entryTtl(Duration.ofMinutes(1)))
                .withCacheConfiguration(CacheNames.PRODUCTS, defaults);
    }
}
