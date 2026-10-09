package com.course.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Checks that Spring MVC really triggers validation and answers 400.
 * MockMvc sends fake HTTP requests without starting a real server
 * (you will learn this properly in topic 46).
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validRegistrationReturns201() throws Exception {
        String json = """
                {"name":"Asha Rao","email":"asha@example.com","password":"Secret@123",
                 "age":25,"address":{"street":"MG Road 1","city":"Bengaluru","pinCode":"560001"}}
                """;
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value("Bengaluru"));
    }

    @Test
    void invalidRegistrationReturns400() throws Exception {
        String json = """
                {"name":"A","email":"nope","password":"weak","age":15,
                 "address":{"street":"x","city":"y","pinCode":"560001"}}
                """;
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void pathVariableBelowMinReturns400() throws Exception {
        mockMvc.perform(get("/api/users/0")).andExpect(status().isBadRequest());
    }

    @Test
    void productCreateWithIdIsRejectedByGroup() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":5,\"name\":\"Pen\",\"price\":10}"))
                .andExpect(status().isBadRequest());
    }
}
