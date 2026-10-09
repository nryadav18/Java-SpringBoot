# 38 - Spring Data JPA Fundamentals

Store Java objects in a database without writing SQL: ORM, JPA, Hibernate, `@Entity`, `JpaRepository`,
entity lifecycle, `ddl-auto`, H2 console, and a full CRUD REST API for **Products**.
Open **lesson.html** in a browser for the full lesson.

## Files
- `pom.xml` - Spring Boot 3.5.6 with web, data-jpa, validation, H2.
- `src/main/resources/application.properties` - H2 datasource, H2 console, `ddl-auto`, `show-sql`, `format_sql`.
- `entity/Product.java` - the JPA entity (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@CreationTimestamp`, `@UpdateTimestamp`).
- `repository/ProductRepository.java` - `JpaRepository<Product, Long>` + two derived queries.
- `dto/ProductRequest.java`, `dto/ProductResponse.java` - records used as API input/output (never expose the entity).
- `service/ProductService.java` - business logic, `findById(...).orElseThrow(...)`, dirty checking.
- `controller/ProductController.java` - REST endpoints `/api/products`.
- `exception/*` - 404 / 409 / 400 errors as `ProblemDetail` JSON.
- `config/DataSeeder.java` - inserts 3 sample products at startup.
- `src/test/.../ProductRepositoryTest.java` - `@DataJpaTest` tests, including entity lifecycle states.
- `requests.http` - sample requests for the VS Code REST Client extension.

## Run
```
cd "D:\Java-SpringBoot\38-Spring-Data-JPA-Fundamentals"
mvn spring-boot:run
```
- API: http://localhost:8080/api/products
- H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:shopdb`, user `sa`, empty password)
- Tests: `mvn test`
