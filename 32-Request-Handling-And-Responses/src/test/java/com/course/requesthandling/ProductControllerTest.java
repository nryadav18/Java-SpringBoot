package com.course.requesthandling;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/*
 * MockMvc tests for every CRUD operation, checking status codes, headers and JSON.
 * @DirtiesContext gives each test a fresh ProductService (fresh sample data).
 * Run with: mvn test
 */
@WebMvcTest(ProductController.class)
@Import({ProductService.class, CorsConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listWithFiltersAndTotalCountHeader() throws Exception {
        mockMvc.perform(get("/api/products").param("category", "electronics").param("sort", "price"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Wireless Mouse"));
    }

    @Test
    void jacksonAnnotationsShapeTheJson() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(50))                 // @JsonProperty("stock")
                .andExpect(jsonPath("$.supplierCode").doesNotExist())     // @JsonIgnore
                .andExpect(jsonPath("$.createdAt").value(org.hamcrest.Matchers.matchesPattern(
                        "\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}")));    // @JsonFormat
    }

    @Test
    void unknownIdGives404() throws Exception {
        mockMvc.perform(get("/api/products/999")).andExpect(status().isNotFound());
    }

    @Test
    void postReturns201WithLocationHeader() throws Exception {
        String json = """
                {"name":"USB-C Cable","category":"electronics","price":199.00,"stock":300}
                """;
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("X-Client-Id", "mobile-app"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/products/5")))
                .andExpect(header().string("X-Created-By", "mobile-app"))
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void putReplacesAndPatchChangesOnlyOneField() throws Exception {
        mockMvc.perform(put("/api/products/3").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Big Java Mug","category":"kitchen","price":349.00,"stock":80}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Big Java Mug"));

        mockMvc.perform(patch("/api/products/3").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\": 299.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(299.00))
                .andExpect(jsonPath("$.name").value("Big Java Mug"));   // unchanged
    }

    @Test
    void deleteReturns204ThenTheProductIsGone() throws Exception {
        mockMvc.perform(delete("/api/products/2")).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/products/2")).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/products/2")).andExpect(status().isNotFound());
    }

    @Test
    void askingForXmlGives406() throws Exception {
        mockMvc.perform(get("/api/products/1").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }
}
