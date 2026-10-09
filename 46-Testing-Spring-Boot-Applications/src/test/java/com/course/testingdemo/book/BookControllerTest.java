package com.course.testingdemo.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * LEVEL 2 - WEB SLICE TEST with @WebMvcTest
 *
 * @WebMvcTest(BookController.class) starts ONLY the web layer:
 *   - our BookController, @RestControllerAdvice, Jackson, validation, MockMvc
 *   - NOT services, repositories, database, or a real server
 *
 * Because BookService is not loaded, we provide a fake one with @MockitoBean.
 * @MockitoBean (Spring Framework 6.2 / Boot 3.4+) replaces the old @MockBean,
 * which is deprecated since Boot 3.4.
 *
 * MockMvc sends "pretend" HTTP requests straight into Spring MVC - no network, no port.
 * We check: URL mapping, status codes, JSON output, validation, error handling.
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;   // to turn Java objects into JSON strings

    @MockitoBean
    private BookService bookService;     // a Mockito mock, registered as a Spring bean

    @Test
    void getAllReturnsJsonArray() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(
                new BookResponse(1L, "Clean Code", "Robert C. Martin", "978-0132350884", new BigDecimal("450.00")),
                new BookResponse(2L, "Effective Java", "Joshua Bloch", "978-0134685991", new BigDecimal("599.00"))));

        mockMvc.perform(get("/api/books"))
                .andDo(print())                                   // prints request + response in the test log
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))             // JSONPath: $ = the root of the JSON
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[1].price").value(599.00));
    }

    @Test
    void getOneReturns404ProblemDetailWhenMissing() throws Exception {
        when(bookService.findById(42L)).thenThrow(new BookNotFoundException(42L));

        mockMvc.perform(get("/api/books/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Book not found"))
                .andExpect(jsonPath("$.detail").value(containsString("42")));
    }

    @Test
    void postCreatesBookAndReturns201WithLocation() throws Exception {
        BookRequest request = new BookRequest("Refactoring", "Martin Fowler", "978-0134757599", new BigDecimal("499.00"));
        when(bookService.create(any(BookRequest.class)))
                .thenReturn(new BookResponse(7L, "Refactoring", "Martin Fowler", "978-0134757599", new BigDecimal("499.00")));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/books/7")))
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void postWithInvalidBodyReturns400AndNeverCallsService() throws Exception {
        String invalidJson = """
                { "title": "", "author": "Someone", "isbn": "123", "price": -1 }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.price").exists());

        verify(bookService, never()).create(any());
    }

    @Test
    void postDuplicateIsbnReturns409() throws Exception {
        when(bookService.create(any())).thenThrow(new DuplicateIsbnException("978-0132350884"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","author":"Robert C. Martin","isbn":"978-0132350884","price":450}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void discountWithInvalidPercentReturns400() throws Exception {
        when(bookService.applyDiscount(1L, 80)).thenThrow(new IllegalArgumentException("Discount must be between 1 and 50 percent"));

        mockMvc.perform(patch("/api/books/1/discount").param("percent", "80"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("between 1 and 50")));
    }

    @Test
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/api/books/3"))
                .andExpect(status().isNoContent());

        verify(bookService).delete(3L);
    }
}
