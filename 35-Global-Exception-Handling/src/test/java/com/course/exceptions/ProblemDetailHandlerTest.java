package com.course.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Same app, "problem" profile: errors follow RFC 9457 (application/problem+json).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("problem")
class ProblemDetailHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void missingBookIsAProblemDetail() throws Exception {
        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://api.library.example/problems/resource-not-found"))
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Book not found with id: 99"))
                .andExpect(jsonPath("$.instance").value("/api/books/99"))
                .andExpect(jsonPath("$.resource").value("Book"));
    }

    @Test
    void validationProblemHasFieldErrors() throws Exception {
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"author\":\"X\",\"isbn\":\"9780000000000\",\"copies\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }
}
