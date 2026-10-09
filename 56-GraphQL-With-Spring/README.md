# 56 - GraphQL with Spring

A small Books & Authors API built with Spring for GraphQL (`spring-boot-starter-graphql`) on Spring MVC.
Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `pom.xml` | starter-graphql + starter-web + spring-graphql-test |
| `src/main/resources/graphql/schema.graphqls` | the schema: `type`, `Query`, `Mutation`, `input` |
| `src/main/resources/application.properties` | `/graphql` path, GraphiQL UI, schema printer |
| `library/Book.java`, `Author.java`, `BookInput.java` | records matching the schema types |
| `library/LibraryRepository.java` | in-memory data + a counter to *see* the N+1 problem |
| `library/BookController.java` | `@QueryMapping`, `@MutationMapping`, `@Argument`, `@SchemaMapping`, `@BatchMapping` |
| `library/GraphQlErrorHandler.java` | `@GraphQlExceptionHandler` -> NOT_FOUND / BAD_REQUEST errors |
| `src/test/.../BookControllerTest.java` | `@GraphQlTest` + `GraphQlTester` (4 tests) |
| `requests.http` | sample POST /graphql requests for the REST Client extension |

## Run
```
mvn spring-boot:run
# open http://localhost:8080/graphiql  and run:  { books { title author { name } } }
mvn test
```
