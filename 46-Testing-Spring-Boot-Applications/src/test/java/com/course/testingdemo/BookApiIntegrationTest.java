package com.course.testingdemo;

import com.course.testingdemo.book.BookRepository;
import com.course.testingdemo.book.BookRequest;
import com.course.testingdemo.book.BookResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * LEVEL 3 - FULL INTEGRATION TEST with @SpringBootTest
 *
 * webEnvironment = RANDOM_PORT:
 *   - starts the WHOLE application (all beans, real H2 database)
 *   - on a real embedded Tomcat, on a free random port (so it never clashes with 8080)
 *
 * TestRestTemplate sends REAL HTTP requests to that server. It already knows the
 * random port, so we write relative URLs like "/api/books".
 * Unlike RestTemplate, it does NOT throw on 4xx/5xx - perfect for checking error codes.
 *
 * properties = "app.seed-data=false" -> no sample books; each test controls its own data.
 * Slowest kind of test: write fewer of these, for the important end-to-end flows.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.seed-data=false")
class BookApiIntegrationTest {

    @LocalServerPort
    private int port;                       // the random port actually used

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository repository;      // the REAL repository - for setup and cleanup

    @AfterEach
    void cleanDatabase() {
        // The server runs in other threads, so there is no automatic rollback here.
        repository.deleteAll();
    }

    @Test
    void fullBookLifecycle() {
        // 1. CREATE
        BookRequest request = new BookRequest("Clean Code", "Robert C. Martin", "978-0132350884", new BigDecimal("450.00"));
        ResponseEntity<BookResponse> created = restTemplate.postForEntity("/api/books", request, BookResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = created.getHeaders().getLocation();
        assertThat(location).isNotNull();
        assertThat(location.toString()).contains("localhost:" + port + "/api/books/");
        Long id = created.getBody().id();

        // 2. READ (follow the Location header)
        ResponseEntity<BookResponse> fetched = restTemplate.getForEntity(location, BookResponse.class);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody().title()).isEqualTo("Clean Code");

        // 3. DISCOUNT 10 %
        ResponseEntity<BookResponse> discounted = restTemplate.exchange(
                "/api/books/{id}/discount?percent=10", HttpMethod.PATCH, null, BookResponse.class, id);
        assertThat(discounted.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(discounted.getBody().price()).isEqualByComparingTo("405.00");

        // ...and it really is stored in the database
        assertThat(repository.findById(id)).get()
                .satisfies(book -> assertThat(book.getPrice()).isEqualByComparingTo("405.00"));

        // 4. DELETE
        ResponseEntity<Void> deleted = restTemplate.exchange("/api/books/{id}", HttpMethod.DELETE, null, Void.class, id);
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(repository.existsById(id)).isFalse();
    }

    @Test
    void duplicateIsbnReturns409() {
        BookRequest request = new BookRequest("Effective Java", "Joshua Bloch", "978-0134685991", new BigDecimal("599.00"));
        restTemplate.postForEntity("/api/books", request, BookResponse.class);

        ResponseEntity<ProblemDetail> second = restTemplate.postForEntity("/api/books", request, ProblemDetail.class);

        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(second.getBody().getTitle()).isEqualTo("Duplicate ISBN");
    }

    @Test
    void unknownBookReturns404() {
        ResponseEntity<ProblemDetail> response = restTemplate.getForEntity("/api/books/12345", ProblemDetail.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getDetail()).contains("12345");
    }
}
