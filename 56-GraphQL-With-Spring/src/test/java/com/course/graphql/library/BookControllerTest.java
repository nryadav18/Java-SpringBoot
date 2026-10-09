package com.course.graphql.library;

/*
 * @GraphQlTest = a "test slice" (Topic 46) that starts only the GraphQL layer:
 * the schema, @Controller classes and GraphQlTester. No web server is started.
 * We @Import the repository and the error handler because they are not part of the slice.
 *
 * Run: mvn test
 */
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(BookController.class)
@Import({LibraryRepository.class, GraphQlErrorHandler.class})
class BookControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private LibraryRepository repository;

    @Test
    void bookByIdReturnsOnlyRequestedFields() {
        graphQlTester.document("""
                        query {
                          bookById(id: 1) {
                            title
                            author { name }
                          }
                        }
                        """)
                .execute()
                .path("bookById.title").entity(String.class).isEqualTo("Effective Java")
                .path("bookById.author.name").entity(String.class).isEqualTo("Joshua Bloch");
    }

    @Test
    void batchMappingLoadsAllAuthorsInOneCall() {
        int before = repository.authorQueryCount();

        graphQlTester.document("{ books { title author { name } } }")
                .execute()
                .path("books").entityList(Object.class).hasSizeGreaterThan(4);

        // 5+ books, but only ONE author lookup thanks to @BatchMapping (no N+1)
        assertThat(repository.authorQueryCount() - before).isEqualTo(1);
    }

    @Test
    void addBookMutationWithVariables() {
        graphQlTester.document("""
                        mutation AddBook($input: BookInput!) {
                          addBook(input: $input) { id title author { name } }
                        }
                        """)
                .variable("input", java.util.Map.of("title", "Spring Boot Up and Running", "pages", 300, "authorId", 3))
                .execute()
                .path("addBook.title").entity(String.class).isEqualTo("Spring Boot Up and Running")
                .path("addBook.author.name").entity(String.class).isEqualTo("Craig Walls");
    }

    @Test
    void deletingMissingBookGivesNotFoundError() {
        graphQlTester.document("mutation { deleteBook(id: 999) }")
                .execute()
                .errors()
                .expect(error -> error.getErrorType() == ErrorType.NOT_FOUND
                        && error.getMessage().contains("999"));
    }
}
