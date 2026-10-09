# 51 - Caching with Redis

Speed up slow reads with Spring's cache abstraction (`@Cacheable`, `@CachePut`, `@CacheEvict`).
The fake `ProductRepository` takes 2 seconds per query, so you can SEE the speed-up.
Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | `spring-boot-starter-cache` + `spring-boot-starter-data-redis` |
| `config/CacheConfig.java` | `@EnableCaching`, Redis TTL + JSON serialization (`redis` profile only) |
| `config/CacheNames.java` | Cache names as constants |
| `product/ProductRepository.java` | Deliberately slow in-memory "database" that counts real hits |
| `product/ProductService.java` | All caching annotations, keys, `condition`, `unless`, `@Caching` |
| `web/ProductController.java` | REST API with an `X-Response-Time-Ms` header |
| `web/CacheAdminController.java` | Look inside / clear caches via `CacheManager` |
| `application.properties` / `application-redis.properties` | In-memory cache (default) vs Redis |
| `ProductServiceCachingTest.java` | Proves cache hits by counting database calls |
| `docker-compose.yml` | Redis 7 server |
| `requests.http` | Sample requests for the VS Code REST Client |

## Run
```
mvn spring-boot:run                                      # in-memory cache, no Redis needed
docker compose up -d                                     # start Redis
mvn spring-boot:run -Dspring-boot.run.profiles=redis     # use Redis
mvn test                                                 # run the caching tests
```
Then call `GET http://localhost:8080/api/products/1` twice and compare the times.
