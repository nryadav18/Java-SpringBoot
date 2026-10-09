package com.course.todoweb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/*
 * Tests for a server-side MVC controller: we check the VIEW name, the MODEL
 * contents, redirects and the rendered HTML.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listPageRendersSampleTodos() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(view().name("todos/list"))
                .andExpect(model().attributeExists("todos", "openCount", "priorities"))
                .andExpect(content().string(containsString("Learn Thymeleaf basics")));
    }

    @Test
    void doneFilterShowsOnlyDoneTodos() throws Exception {
        mockMvc.perform(get("/todos").param("filter", "done"))
                .andExpect(model().attribute("todos", hasSize(1)));
    }

    @Test
    void invalidFormIsShownAgainWithErrors() throws Exception {
        mockMvc.perform(post("/todos").param("title", "").param("priority", "HIGH"))
                .andExpect(status().isOk())                                  // NOT a redirect
                .andExpect(view().name("todos/form"))
                .andExpect(model().attributeHasFieldErrors("todoForm", "title"))
                .andExpect(content().string(containsString("Please enter a title")));
    }

    @Test
    void validFormRedirectsWithFlashMessage() throws Exception {
        mockMvc.perform(post("/todos").param("title", "Write tests").param("priority", "LOW"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"))
                .andExpect(flash().attribute("successMessage", "Todo 'Write tests' was added."));
    }

    @Test
    void unknownTodoGives404() throws Exception {
        mockMvc.perform(get("/todos/999/edit"))
                .andExpect(status().isNotFound());
    }
}
