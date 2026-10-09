# 41 - Pagination & Sorting

Never return a million rows: `Pageable`, `PageRequest`, `Sort`, `Page` vs `Slice`, controller
`page`/`size`/`sort` parameters, a clean response shape (`content`, `totalElements`, `totalPages`),
`PagedModel` and `spring.data.web.pageable.*` settings, safe limits, and search filters + paging together.
Domain: a catalogue of 60 products. Open **lesson.html** in a browser for the full lesson.

## Files
- `pom.xml` - Spring Boot 3.5.6 with web, data-jpa, H2.
- `application.properties` - `spring.data.web.pageable.*` defaults, max page size, `serialization-mode=via-dto`.
- `entity/Product.java` - entity with database indexes on `category` and `price`.
- `repository/ProductRepository.java` - `Page`, `Slice`, `Sort` and `@Query` + `Pageable` methods.
- `repository/ProductSpecifications.java` - filters for the search endpoint.
- `dto/PageResponse.java`, `dto/SliceResponse.java`, `dto/ProductResponse.java` - response records.
- `service/ProductService.java` - all paging variants + sort-field whitelist.
- `controller/ProductController.java` - endpoints with `Pageable` + `@PageableDefault`.
- `exception/GlobalExceptionHandler.java` - 400 for bad paging/sort input.
- `config/DataSeeder.java` - 60 predictable products.
- `src/test/...` - `@DataJpaTest` paging tests + MockMvc tests of the query parameters.
- `requests.http` - sample requests.

## Run
```
cd "D:\Java-SpringBoot\41-Pagination-And-Sorting"
mvn spring-boot:run
```
Try http://localhost:8080/api/products?page=0&size=5&sort=price,desc . Tests: `mvn test`
