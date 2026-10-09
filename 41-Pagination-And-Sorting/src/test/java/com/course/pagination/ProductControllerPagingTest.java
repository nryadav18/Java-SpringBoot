package com.course.pagination;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * Web-level tests: does Spring turn ?page=&size=&sort= into the right Pageable?
 * MockMvc sends fake HTTP requests without starting a real server (explained fully in topic 46).
 * The DataSeeder runs here, so there are 60 products.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerPagingTest {

    @Autowired
    MockMvc mvc;

    @Test
    void pageSizeAndSortFromQueryParameters() throws Exception {
        mvc.perform(get("/api/products").param("page", "0").param("size", "5").param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[0].name").value("Scarf"))
                .andExpect(jsonPath("$.totalElements").value(60))
                .andExpect(jsonPath("$.totalPages").value(12))
                .andExpect(jsonPath("$.first").value(true));
    }

    @Test
    void defaultsAreUsedWhenNothingIsSent() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void hugePageSizeIsCappedByMaxPageSize() throws Exception {
        mvc.perform(get("/api/products").param("size", "100000"))
                .andExpect(jsonPath("$.size").value(50));
    }

    @Test
    void unknownSortFieldGives400() throws Exception {
        mvc.perform(get("/api/products").param("sort", "password,asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchCombinesFiltersWithPaging() throws Exception {
        mvc.perform(get("/api/products/search").param("category", "Books").param("size", "5"))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void pagedModelShape() throws Exception {
        mvc.perform(get("/api/products/paged-model").param("size", "3"))
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.page.size").value(3))
                .andExpect(jsonPath("$.page.totalElements").value(60));
    }
}
