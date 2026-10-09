# 39 - JPA Queries: Derived, JPQL & Native

All the ways to ask the database questions with Spring Data JPA: derived query methods,
`@Query` with JPQL, native SQL, `@Modifying` bulk updates/deletes, projections (interface & record),
Specifications and Query by Example. Domain: a **library** of 13 books.
Open **lesson.html** in a browser for the full lesson.

## Files
- `pom.xml` - Spring Boot 3.5.6 with web, data-jpa, H2.
- `src/main/resources/application.properties` - H2, SQL logging, `defer-datasource-initialization`.
- `src/main/resources/data.sql` - 13 sample books inserted at startup.
- `entity/Book.java`, `entity/Genre.java` - entity with an `@Enumerated(EnumType.STRING)` field.
- `repository/BookRepository.java` - **the heart of the topic**: every query style, heavily commented.
- `repository/BookSpecifications.java` - reusable `Specification<Book>` filters.
- `dto/BookResponse.java` (record), `dto/BookSummary.java` (interface projection),
  `dto/AuthorStats.java` (record projection), `dto/GenreCount.java` (native projection).
- `service/BookService.java` - calls the queries; Specifications + Query by Example; `@Transactional` for bulk ops.
- `controller/BookController.java` - one endpoint per query style under `/api/books`.
- `exception/*` - 404 / 400 as `ProblemDetail`.
- `src/test/.../BookRepositoryTest.java` - `@DataJpaTest` tests for every query.
- `requests.http` - all endpoints, ready to click.

## Run
```
cd "D:\Java-SpringBoot\39-JPA-Queries-Derived-JPQL-Native"
mvn spring-boot:run
```
Try http://localhost:8080/api/books/search?keyword=tolkien and watch the SQL in the console.
Tests: `mvn test`
