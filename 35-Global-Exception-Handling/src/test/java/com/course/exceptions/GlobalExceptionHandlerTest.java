package com.course.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Verifies the ErrorResponse format produced by GlobalExceptionHandler (default profile).
 */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void missingBookGives404WithStandardBody() throws Exception {
        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Book not found with id: 99"))
                .andExpect(jsonPath("$.path").value("/api/books/99"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }

    @Test
    void invalidBodyGives400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"author\":\"X\",\"isbn\":\"123\",\"copies\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors", hasSize(3)))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("copies"));
    }

    @Test
    void duplicateIsbnGives409() throws Exception {
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Dup\",\"author\":\"X\",\"isbn\":\"9780132350884\",\"copies\":1}"))
                .andExpect(status().isConflict());
    }

    @Test
    void borrowingUnavailableBookGives422() throws Exception {
        mockMvc.perform(post("/api/books/3/borrow"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void wrongTypeAndBrokenJsonGive400() throws Exception {
        mockMvc.perform(get("/api/books/abc")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content("{\"title\": \"oops"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body is missing or is not valid JSON"));
    }

    @Test
    void invalidQueryParamGives400() throws Exception {
        mockMvc.perform(get("/api/books/available").param("minCopies", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("minCopies"));
    }

    @Test
    void bugGives500WithoutLeakingDetails() throws Exception {
        mockMvc.perform(get("/api/books/crash"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(not(containsString("NullPointer"))))
                .andExpect(content().string(not(containsString("toUpperCase"))));
    }
}
