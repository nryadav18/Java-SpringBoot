# 40 - JPA Relationships

Linking entities: `@OneToOne`, `@OneToMany`/`@ManyToOne` (bidirectional, `mappedBy`, owning side),
`@ManyToMany` with a join table, `@JoinColumn`, cascade types, `orphanRemoval`, LAZY vs EAGER,
`LazyInitializationException`, the N+1 problem (JOIN FETCH, `@EntityGraph`) and JSON infinite recursion.
Domain: Customer - Address (1:1), Customer - Orders (1:N), Order - Product (N:N).
Open **lesson.html** in a browser for the full lesson.

## Files
- `pom.xml` - Spring Boot 3.5.6 with web, data-jpa, validation, H2.
- `application.properties` - SQL logging, `open-in-view=false`, Hibernate statistics for the N+1 demo.
- `entity/Customer.java` - owning side of 1:1 (`address_id`), inverse side of 1:N (`mappedBy`), helper methods.
- `entity/Address.java` - unidirectional 1:1 target.
- `entity/PurchaseOrder.java` - `@ManyToOne` owning side, `@ManyToMany` + `@JoinTable` owning side (table `orders`).
- `entity/Product.java` - inverse side of N:N (`mappedBy`), `@JsonIgnore`.
- `repository/CustomerRepository.java` - `JOIN FETCH` and `@EntityGraph` queries (N+1 fixes).
- `repository/PurchaseOrderRepository.java` - derived queries through relationships.
- `dto/Dtos.java` - request/response records (the real fix for JSON recursion).
- `service/CustomerService.java` - cascade, orphan removal, N+1 measurements, LazyInitializationException demo.
- `controller/*` - REST endpoints, including `/demo/...` endpoints.
- `config/DataSeeder.java` - builds the object graph and saves it with one `saveAll` (cascade).
- `src/test/.../RelationshipsTest.java` - 7 `@SpringBootTest` tests proving each behaviour.
- `requests.http` - all requests.

## Run
```
cd "D:\Java-SpringBoot\40-JPA-Relationships"
mvn spring-boot:run
```
Then open http://localhost:8080/api/customers/demo/n-plus-one and watch the console. Tests: `mvn test`
