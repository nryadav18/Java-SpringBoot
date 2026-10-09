package com.course.restbasics;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/*
 * A first look at testing a REST controller WITHOUT starting a real server.
 * MockMvc sends fake HTTP requests through the real DispatcherServlet.
 * (Topic 46 explains @WebMvcTest and MockMvc in depth.)
 * Run with: mvn test
 */
@WebMvcTest(BookController.class)
@Import(BookService.class)   // @WebMvcTest only loads web beans, so we add our real service
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllBooksReturnsTheSampleBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void unknownBookGives404() throws Exception {
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postingABookReturns201WithANewId() throws Exception {
        String json = """
                {"title":"Java Concurrency in Practice","author":"Brian Goetz","year":2006,"price":800}
                """;
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.author").value("Brian Goetz"));
    }
}
