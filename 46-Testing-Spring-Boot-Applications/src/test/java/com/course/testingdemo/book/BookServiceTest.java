package com.course.testingdemo.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * LEVEL 1 - PURE UNIT TEST (the base of the test pyramid)
 *
 * - NO Spring at all: no application context, no database, no web server.
 * - The repository is a Mockito MOCK: a fake object whose answers we script.
 * - Runs in milliseconds. Write MANY of these for your business rules.
 *
 * @ExtendWith(MockitoExtension.class) -> lets Mockito create the @Mock / @InjectMocks fields.
 * @Mock        -> creates a fake BookRepository
 * @InjectMocks -> creates a REAL BookService and passes the mock into its constructor
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookService service;

    // Small helper to build a book that "already has" an id, like one from the database
    private static Book book(Long id, String title, String price) {
        Book book = new Book(title, "Some Author", "ISBN-" + id, new BigDecimal(price));
        book.setId(id);
        return book;
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns the book when it exists")
        void returnsBook() {
            // Arrange (Given): script the mock
            when(repository.findById(1L)).thenReturn(Optional.of(book(1L, "Clean Code", "450.00")));

            // Act (When)
            BookResponse result = service.findById(1L);

            // Assert (Then)
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("Clean Code");
        }

        @Test
        @DisplayName("throws BookNotFoundException when the id is unknown")
        void throwsWhenMissing() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(BookNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves a new book with trimmed text and a 2-decimal price")
        void savesNewBook() {
            BookRequest request = new BookRequest("  Refactoring ", " Martin Fowler ", "978-0134757599",
                    new BigDecimal("499.999"));
            when(repository.existsByIsbn("978-0134757599")).thenReturn(false);
            // thenAnswer: return whatever was passed to save(...) - like a real save would
            when(repository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

            service.create(request);

            // ArgumentCaptor "catches" the object our service passed to the mock,
            // so we can look inside it.
            ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
            verify(repository).save(captor.capture());
            Book saved = captor.getValue();
            assertThat(saved.getTitle()).isEqualTo("Refactoring");
            assertThat(saved.getAuthor()).isEqualTo("Martin Fowler");
            assertThat(saved.getPrice()).isEqualByComparingTo("500.00");
        }

        @Test
        @DisplayName("rejects a duplicate ISBN and never calls save()")
        void rejectsDuplicateIsbn() {
            BookRequest request = new BookRequest("Clean Code", "Robert C. Martin", "978-0132350884",
                    new BigDecimal("450.00"));
            when(repository.existsByIsbn("978-0132350884")).thenReturn(true);

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(DuplicateIsbnException.class);

            // verify(..., never()) = make sure something did NOT happen
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("applyDiscount")
    class ApplyDiscount {

        // One test method, many inputs: @ParameterizedTest + @CsvSource (topic 21)
        @ParameterizedTest(name = "{1}% off {0} = {2}")
        @CsvSource({
                "100.00, 10, 90.00",
                "450.00, 50, 225.00",
                "99.99,  1,  98.99",
                "10.00,  33, 6.70"
        })
        void reducesPrice(String originalPrice, int percent, String expectedPrice) {
            Book book = book(1L, "Any Book", originalPrice);
            when(repository.findById(1L)).thenReturn(Optional.of(book));
            when(repository.save(book)).thenReturn(book);

            BookResponse result = service.applyDiscount(1L, percent);

            // isEqualByComparingTo ignores scale: 90.0 equals 90.00
            assertThat(result.price()).isEqualByComparingTo(expectedPrice);
        }

        @ParameterizedTest(name = "{0}% is rejected")
        @ValueSource(ints = {-5, 0, 51, 100})
        void rejectsInvalidPercent(int percent) {
            assertThatThrownBy(() -> service.applyDiscount(1L, percent))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between 1 and 50");

            // The rule is checked BEFORE touching the database
            verify(repository, never()).findById(any());
        }
    }

    @Test
    @DisplayName("findAll maps every entity to a response")
    void findAllMapsEntities() {
        when(repository.findAll()).thenReturn(List.of(book(1L, "A", "10.00"), book(2L, "B", "20.00")));

        List<BookResponse> result = service.findAll();

        assertThat(result).extracting(BookResponse::title).containsExactly("A", "B");
    }

    @Test
    @DisplayName("delete removes an existing book")
    void deleteRemovesBook() {
        Book existing = book(5L, "Old Book", "5.00");
        when(repository.findById(5L)).thenReturn(Optional.of(existing));

        service.delete(5L);

        verify(repository).delete(existing);
    }
}
